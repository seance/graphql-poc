FROM abiosoft/caddy:0.9.3
MAINTAINER github/seance

# Install Node.js
RUN apk --update add nodejs=6.7.0-r0

# Copy source
RUN mkdir -p /app /app/app
COPY package.json /app
COPY npm-shrinkwrap.json /app
COPY querygen.js /app
COPY app /app/app

# Build distribution & copy in place
WORKDIR /app
RUN npm install \
  && npm run dist \
  && rm -rf /srv \
  && cp -r /app/dist /srv

# Copy Caddyfile
COPY Caddyfile /etc/Caddyfile

EXPOSE 8000
WORKDIR /srv
