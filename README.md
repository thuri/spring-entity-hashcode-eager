# Spring Entity HashCode Eager Issue Example

This project demonstrates a potential issue with Spring Data JPA or hibernate when implementing
`equals` and `hashCode` on a field of an associated entity that is fetched with fetch type EAGER
when the associated entity is fetched by a repository method.

## Project Structure

The project uses the following entities:

- **Library**: Represents a library, which can have many books and subsidiaries.
- **Book**: Represents a book, with a title and genre, and belongs to a library. hashcode and equals are overridden and
  based on the title field. The title field is not nullable and should always be present and is not changeable.
- **Subsidiary**: Represents a subsidiary of the library. I needed another one-to-many relationship and that's the only
  reason this entity exists.

The relationships are:

- Library --1:N--> Books
- Library --1:N--> Subsidiaries

## Key Points

- Entities use `UUID` as primary keys.
- Collections are managed with `MutableSet`.
- The `Book` entity overrides `equals` and `hashCode` based on the title.
- The test demonstrates that removing an entity is sometimes not possible

## Problem description - TLDR

When implementing `equals` and `hashCode` it is sometimes not possible to remove participants via `Set#remove` method.

## Problem description - Details

When we get a library entity by calling the `LibraryRepository#findById` method we get the object and the books set is
already initialized. We can successfully remove an object from the set:

```kotlin
  libraryRepository.findById(libraryId).orElseThrow().let { it ->
    assertThat(it.books.remove(it.books.find { it.title == "Book Two" })).isTrue
  }
```

But removing a child is no longer possible when we start at the `BookRepository` and call a query method like
`findByTitleAndGenre`:

```kotlin
  val removed = bookRepository.findByTitleAndGenre(title = "Book One", genre = Genre.BIOGRAPHY).first().let {
    it.library!!.books.remove(it)
  }
```

That test fails but it should not.

## Analysis already conducted

When the `findByTitleAndGenre` method is called, Hibernate/JPA/Spring data runs a `LEFT JOIN` query joining all three
tables.

While creating the objects the Book objects are created using their default constructor.
This means that all fields in the JVM Object are actually null if they are not initialized by default, which the `title`
attribute is not.

Before initializing the fields the objects seem to be put in the Set of the `books` field in the `library` object.
When a book is put into the set it's `hashCode` is called. Because `title` is null at that point the hashCode would be 0.

It seems as if the fields of the book objects are set after the books have been added to the set.

So when we call `Set#remove` later with an `Book` object as parameter, the hashCode method is called on the parameter. 
Because the title field is set now the result will be the `hashCode` of the value of the `title` attribute.
That value won't be 0 and therefore the `Set#remove` call won't find the object and return `false`.

## Possible workarounds

### FetchType.LAZY

switching the fetch type to LAZY seems to resolve the issue (see branch "fetchtype.lazy") but it may not be the intended
use.

## Technologies Used

- Kotlin
- Spring Boot
- Spring Data JPA
- H2 Database (in-memory)
- AssertJ (for assertions in tests)

## How to Run

1. Build the project using Gradle.
2. Run the tests to see the entity relationship and collection manipulation in action.


