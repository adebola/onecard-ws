FROM nginx
USER root
RUN mkdir -p /etc/letsencrypt/live/auth.factorialsystems.io
COPY letsencrypt-old/fullchain.pem /etc/letsencrypt/live/auth.factorialsystems.io
COPY letsencrypt-old/README /etc/letsencrypt/live/auth.factorialsystems.io
COPY letsencrypt-old/cert.pem /etc/letsencrypt/live/auth.factorialsystems.io
COPY letsencrypt-old/chain.pem /etc/letsencrypt/live/auth.factorialsystems.io
COPY letsencrypt-old/privkey.pem /etc/letsencrypt/live/auth.factorialsystems.io
COPY letsencrypt-old/options-ssl-nginx.conf /etc/letsencrypt
COPY letsencrypt-old/ssl-dhparams.pem /etc/letsencrypt
COPY ./default.conf /etc/nginx/conf.d
