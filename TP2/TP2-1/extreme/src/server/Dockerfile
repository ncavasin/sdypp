FROM node:17-alpine3.14
RUN mkdir /server
COPY index.js /server
COPY package.json /server
COPY address.json /server
COPY /public /server/public
COPY /views /server/views
COPY /controllers /server/controllers
WORKDIR /server
RUN npm install 
EXPOSE 3000
CMD node index.js