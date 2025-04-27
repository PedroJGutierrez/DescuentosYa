const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const dotenv = require('dotenv');
const userRoutes = require('./routes/userRoutes'); // Tu archivo de rutas

dotenv.config(); // Cargar las variables de entorno

const app = express();
app.use(cors());
app.use(express.json()); // Para manejar datos JSON

// Usar las rutas de usuarios
app.use('/api', userRoutes); // Ruta base para la API

// Conectar a MongoDB
mongoose.connect(process.env.MONGO_URI, {
    dbName: 'descuentosya',
    useNewUrlParser: true,
    useUnifiedTopology: true
})
.then(() => console.log('Conexión a MongoDB exitosa'))
.catch((error) => console.error('Error de conexión a MongoDB:', error));

app.listen(3001, '0.0.0.0', () => {
    console.log('API corriendo en http://0.0.0.0:3001');
});