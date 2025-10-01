FROM nginx:alpine

#copy content of dist folder into nginx html folder
COPY dist/bank_app/browser /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY docker_entrypoint.sh /docker_entrypoint.sh

EXPOSE 80

RUN chmod +x /docker_entrypoint.sh

CMD ["/docker_entrypoint.sh"]
