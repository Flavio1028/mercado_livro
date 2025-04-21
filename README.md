## 📚 Mercado Livro

O **Mercado Livro** é uma aplicação que simula uma livraria online.  
Com ela, é possível:

- Cadastrar usuários
- Cadastrar livros
- Gerenciar as vendas dos livros

> ⚠️ **Atenção:** Para acessar os endpoints da aplicação, é necessário utilizar um token de autenticação JWT.

## Tecnologias Utilizadas

- Spring Boot 2.4.3
- Spring Security
- Java 11
- kotlin 1.4.30
- Swagger 2.9.2
- JWT 0.9.1
- JUnit 5

* Nota: O projeto foi originalmente criado com Java 11 e Spring Boot 2.4.3 .*

### 🚀 Subindo o Projeto

A aplicação utiliza, por padrão, o perfil **MySQL**. Para executá-la com esse perfil, basta subir os containers com Docker.  
Se preferir não utilizar um banco de dados externo, você pode optar pelo perfil **H2**, que roda em memória.

#### 📦 Executar com Docker (Perfil MySQL)

1. Certifique-se de que o **Docker** e o **Docker Compose** estão instalados na sua máquina.
2. No diretório raiz do projeto, execute o comando abaixo para iniciar os containers:

```bash
    docker-compose up
```

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).