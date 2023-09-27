FROM nginx:latest
COPY self-signed-certificate/fullchain.pem /etc/ssl/certs
COPY self-signed-certificate/privkey.pem /etc/ssl/private
COPY default.conf /etc/nginx/conf.d/default.conf
RUN unlink /var/log/nginx/access.log
RUN unlink /var/log/nginx/error.log