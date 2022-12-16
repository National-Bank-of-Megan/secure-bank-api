FROM nginx:latest
COPY certificate/nbm.crt /etc/ssl/certs
COPY certificate/nbm.key /etc/ssl/private
ADD certificate/password /etc/ssl/password
COPY default.conf /etc/nginx/conf.d/default.conf
RUN unlink /var/log/nginx/access.log
RUN unlink /var/log/nginx/error.log