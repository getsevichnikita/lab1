# Spring REST API на тему "Библиотека"
Программа работает с классом Book, содержащий информацию о названии книги, её авторе, о том, в каком году она была опубликована.

Класс BookMapper конвертирует объект Book в готовый для ответа на запрос GET Data Transfer Object - BookDTO.

В BookController реализованы методы getBookById, принимающий @PathVariable и возвращающий BookDTO и метод getBooks, принимающий @RequestParam и возвращающий список BookDTO.

В BookService реализованы методы getBookById, принимающий id книги и возвращающий, если книга с таким id найдена, объект Book и getBooksFiltered принимающий остальные поля книги и возвращающий список объектов Book.

В BookRepository реализованы методы, сравнивающие данные в списке репозитория, с данными в запросе пользователя с помощью функций библиотеки java.util

В проекте подключен checkstyle

# JPA (Hibernate/Spring Data)
Подключена БД PostgreSQL. Реализованы сущности Author, Category, Reader, Loan.

ManyToMany: Author и Book, Category и Book. OneToMany: Reader и Loan.

CascadeType выбран стандартный для каждой связи. FetchType только LAZY

Проблема N+1 демонстрируется на методе GET поиска всех сущностей Reader в БД, решается с помощью @EntityGraph.

Реализован метод PATCH изменения списка Loan для Reader, результат его работы сравнивается с аналогом с @Transactional


SonarCloud: https://sonarcloud.io/organizations/getsevichnikita/projects