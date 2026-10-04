const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  // Backend API
  app.use(
    ['/api', '/auth', '/notifications'],
    createProxyMiddleware({ target: 'http://localhost:8080', changeOrigin: true })
  );
  // Auth module dev server — for AuthGuard redirects back to login
  app.use(
    '/client/auth',
    createProxyMiddleware({ target: 'http://localhost:3000', changeOrigin: true })
  );
};
