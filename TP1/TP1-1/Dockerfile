FROM node:17-alpine3.14
RUN mkdir /TP1-1
COPY app.js /TP1-1
COPY package.json /TP1-1
COPY /cliente /TP1-1/cliente
WORKDIR /TP1-1
RUN npm install
EXPOSE 3000
CMD  node app.js