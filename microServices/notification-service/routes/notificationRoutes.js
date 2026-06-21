const express = require('express');
const router = express.Router();
const controller = require('../controllers/notificationController');

// POST /notifications
router.post('/', controller.createNotification);

// GET /notifications
router.get('/', controller.getAllNotifications);

// GET /notifications/user/:userId
router.get('/user/:userId', controller.getUserNotifications);

// PUT /notifications/:id/read
router.put('/:id/read', controller.markAsRead);

// DELETE /notifications/:id
router.delete('/:id', controller.deleteNotification);

module.exports = router;
