FROM nginx:latest
COPY certificate/fullchain.pem /etc/ssl/certs
COPY certificate/privkey.pem /etc/ssl/private
#ADD certificate/password /etc/ssl/password
COPY default.conf /etc/nginx/conf.d/default.conf
RUN unlink /var/log/nginx/access.log
RUN unlink /var/log/nginx/error.log