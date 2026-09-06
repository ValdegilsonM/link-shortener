# ETAPA 1: Construção (Build)
# Usamos o Node.js para instalar as dependências e gerar o HTML/CSS/JS final
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# ETAPA 2: Servidor Web (Nginx)
# O React compilado são apenas arquivos estáticos. Usamos o Nginx, um servidor web ultrarrápido, para servi-los.
FROM nginx:alpine
# Copiamos os arquivos gerados no build para a pasta padrão do Nginx
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
# Ligamos o Nginx em primeiro plano
CMD ["nginx", "-g", "daemon off;"]