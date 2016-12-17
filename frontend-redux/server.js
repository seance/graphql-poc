const path = require('path');
const express = require('express');
const httpProxy = require('http-proxy');

const backend1 = {
  host: process.env.BACKEND1_HOST || 'localhost',
  port: process.env.BACKEND1_PORT || 8080
};

const backend2 = {
  host: process.env.BACKEND2_HOST || 'localhost',
  port: process.env.BACKEND2_PORT || 8081
};

const backend3 = {
  host: process.env.BACKEND3_HOST || 'localhost',
  port: process.env.BACKEND3_PORT || 8082
};

const indexHtml = path.join(__dirname, 'app', 'index.html');
const proxyTarget1 = `http://${backend1.host}:${backend1.port}`;
const proxyTarget2 = `http://${backend2.host}:${backend2.port}`;
const proxyTarget3 = `http://${backend3.host}:${backend3.port}`;

const proxy = httpProxy.createProxyServer();

proxy.on('error', (err, req, res) => {
  console.error('Error', err.message, err.stack);
  res.end();
});

const server = express()
  .all('/backend1/*', (req, res) => {
    req.url = req.url.slice('/backend1/'.length);
    setTimeout(function() {
      proxy.web(req, res, { target: proxyTarget1 });
    }, Math.random() * 1000)
  })
  .all('/backend2/*', (req, res) => {
    req.url = req.url.slice('/backend2/'.length);
    proxy.web(req, res, { target: proxyTarget2 });
  })
  .all('/backend3/*', (req, res) => {
    req.url = req.url.slice('/backend3/'.length);
    proxy.web(req, res, { target: proxyTarget3 });
  })
  .get('*', express.static('app'))
  .get('*', (req, res) => res.sendFile(indexHtml))
  .listen(8000, 'localhost', null, () => {
    const { address, port } = server.address();
    console.log(`Server listening on ${address}:${port}`);
  });
