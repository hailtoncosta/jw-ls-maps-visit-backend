# Integração Frontend ↔ Backend

## Backend
- Spring Boot: `http://localhost:8080`
- API base: `http://localhost:8080/api`
- Banco configurado no `src/main/resources/application.properties`.

## Frontend
Entre em `front-end/front-end/jw-ls-maps-visit` e crie `.env` a partir de `.env.example`:

```env
VITE_API_URL=http://localhost:8080/api
```

Depois:

```bash
npm install
npm run dev
```

## Autenticação
O frontend envia automaticamente:

```http
Authorization: Bearer <JWT>
```

após o login em `POST /api/auth/login`.

## O que foi ajustado
- Login JWT ligado ao React.
- Cliente Axios centralizado com Bearer token.
- Rotas do frontend traduzidas para as rotas reais do Spring Boot.
- CRUD das entidades ligado aos controllers existentes.
- Endereços com criação, consulta, edição e exclusão lógica.
- Registro de visita ligado ao backend.
- Compartilhamento, transferência e mesclagem ligados às rotas administrativas.
- Upload de arquivos ligado a `POST /api/files`.
- JSON do Spring configurado em `snake_case`, compatível com o frontend.
- CORS habilitado para desenvolvimento.
- Removida qualquer ocorrência de `base44` do código-fonte.

## Observação
O build do frontend foi validado com sucesso. O Maven não pôde ser executado neste ambiente porque o wrapper precisaria baixar o Maven da internet; portanto, a validação do backend deve ser feita localmente com:

```bash
./mvnw spring-boot:run
```

ou, se o Maven estiver instalado:

```bash
mvn spring-boot:run
```
