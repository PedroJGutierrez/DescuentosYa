const express = require('express');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const router = express.Router();

// Ruta de Registro
router.post('/register', async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ message: 'Faltan datos' });
    }

    try {
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ message: 'El email ya está registrado' });
        }

        const hashedPassword = await bcrypt.hash(password, 10);
        const newUser = new User({ email, password: hashedPassword });
        await newUser.save();

        res.status(201).json({ success: true, message: 'Usuario registrado exitosamente' });
    } catch (error) {
        console.error('Error al registrar usuario:', error.message); // Mejor log de error
        res.status(500).json({ success: false, message: 'Error en el servidor', error: error.message });
    }
});

// 🚀 Nueva Ruta de Login
router.post('/login', async (req, res) => {
    
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ success: false, message: 'Faltan datos' });
    }

    try {
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ success: false, message: 'Usuario no encontrado' });
        }

        const isPasswordValid = await bcrypt.compare(password, user.password);
        if (!isPasswordValid) {
            return res.status(400).json({ success: false, message: 'Contraseña incorrecta' });
        }

        // Por simplicidad, devolvemos el ID como "token" (podrías mejorar esto luego)
        const jwt = require('jsonwebtoken');

const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, { expiresIn: '1h' });
res.status(200).json({ success: true, message: 'Login exitoso', token });
    } catch (error) {
        console.error('Error en login:', error);
        res.status(500).json({ success: false, message: 'Error en el servidor' });
    }
});

module.exports = router;
