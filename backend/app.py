"""API local de NotasApp: login real, tokens y notas por usuario."""
import hashlib
import secrets
import sqlite3
import time
from pathlib import Path
from functools import wraps
from flask import Flask, request, jsonify, g
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 65536
app.config['DATABASE'] = str(Path(__file__).with_name('notas.db'))

def database():
    if 'db' not in g:
        g.db = sqlite3.connect(app.config['DATABASE'])
        g.db.row_factory = sqlite3.Row
        g.db.execute('PRAGMA foreign_keys = ON')
    return g.db

@app.teardown_appcontext
def close_database(error):
    db = g.pop('db', None)
    if db is not None:
        db.close()

def initialize():
    database().executescript('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY, name TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL);
        CREATE TABLE IF NOT EXISTS tokens (
            token_hash TEXT PRIMARY KEY, user_id INTEGER NOT NULL REFERENCES users(id),
            expires INTEGER NOT NULL);
        CREATE TABLE IF NOT EXISTS notes (
            id INTEGER PRIMARY KEY, user_id INTEGER NOT NULL REFERENCES users(id),
            title TEXT NOT NULL, content TEXT NOT NULL,
            created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ','now')));
    ''')
    database().commit()

def token_hash(value):
    return hashlib.sha256(value.encode()).hexdigest()

def authenticated(function):
    @wraps(function)
    def wrapper(*args, **kwargs):
        header = request.headers.get('Authorization', '')
        if not header.startswith('Bearer '):
            return jsonify(message='Debes iniciar sesión.'), 401
        row = database().execute(
            'SELECT user_id FROM tokens WHERE token_hash=? AND expires>?',
            (token_hash(header[7:]), int(time.time()))).fetchone()
        if row is None:
            return jsonify(message='Sesión vencida. Inicia sesión nuevamente.'), 401
        g.user_id = row['user_id']
        return function(*args, **kwargs)
    return wrapper

@app.get('/api/health')
def health():
    return jsonify(message='Backend de NotasApp funcionando')

@app.post('/api/login')
def login():
    data = request.get_json(silent=True)
    if not isinstance(data, dict):
        return jsonify(message='Envía un objeto JSON válido.'), 400
    email, password = data.get('email'), data.get('password')
    if not isinstance(email, str) or not isinstance(password, str):
        return jsonify(message='Ingresa correo y contraseña.'), 400
    user = database().execute('SELECT * FROM users WHERE email=?', (email.strip().lower(),)).fetchone()
    if user is None or not check_password_hash(user['password_hash'], password):
        return jsonify(message='Correo o contraseña incorrectos.'), 401
    token = secrets.token_urlsafe(32)
    database().execute('DELETE FROM tokens WHERE expires<=?', (int(time.time()),))
    database().execute('INSERT INTO tokens VALUES (?,?,?)',
                       (token_hash(token), user['id'], int(time.time()) + 86400))
    database().commit()
    return jsonify(token=token, name=user['name'])

@app.route('/api/notes', methods=['GET', 'POST'])
@authenticated
def notes():
    db = database()
    if request.method == 'GET':
        rows = db.execute('SELECT id,title,content,created_at FROM notes WHERE user_id=? ORDER BY id DESC',
                          (g.user_id,)).fetchall()
        return jsonify([dict(row) for row in rows])
    data = request.get_json(silent=True)
    if not isinstance(data, dict):
        return jsonify(message='Envía un objeto JSON válido.'), 400
    title, content = data.get('title'), data.get('content')
    if not isinstance(title, str) or not isinstance(content, str):
        return jsonify(message='Título y contenido son obligatorios.'), 400
    title, content = title.strip(), content.strip()
    if not title or not content or len(title)>120 or len(content)>10000:
        return jsonify(message='Usa un título de 1 a 120 caracteres y contenido de 1 a 10000.'), 400
    db.execute('INSERT INTO notes(user_id,title,content) VALUES (?,?,?)', (g.user_id,title,content))
    db.commit()
    return jsonify(message='Nota guardada correctamente.'), 201

if __name__ == '__main__':
    from getpass import getpass
    with app.app_context():
        initialize()
        if database().execute('SELECT COUNT(*) FROM users').fetchone()[0] == 0:
            print('PRIMER INICIO: crea tu usuario para entrar en la aplicación.')
            name = input('Nombre [Luis Antonio Ortiz Chiroy]: ').strip() or 'Luis Antonio Ortiz Chiroy'
            email = input('Correo [luis@notas.local]: ').strip().lower() or 'luis@notas.local'
            while True:
                password = getpass('Crea una contraseña (mínimo 8 caracteres; no se verá al escribir): ')
                if len(password)>=8:
                    break
                print('La contraseña debe tener al menos 8 caracteres.')
            database().execute('INSERT INTO users(name,email,password_hash) VALUES (?,?,?)',
                               (name,email,generate_password_hash(password)))
            database().commit()
            print('Usuario creado. Usa ese correo y contraseña en la app.')
    print('Deja esta ventana abierta. Estado: http://127.0.0.1:5000/api/health')
    app.run(host='127.0.0.1', port=5000, debug=False)
