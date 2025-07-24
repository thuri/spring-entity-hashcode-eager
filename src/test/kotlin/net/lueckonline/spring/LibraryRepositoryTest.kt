package net.lueckonline.spring

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
open class LibraryRepositoryTest(
    @param:Autowired val libraryRepository: LibraryRepository,
    @param:Autowired val entityManager: TestEntityManager,
) {

    @Test
    fun `can save and find library with books`() {
        val libraryId = libraryRepository.save(
            Library(
                name = "Central Library",
                books = mutableSetOf(
                    Book(title = "Book One"),
                    Book(title = "Book Two"),
                    Book(title = "Book Three")
                )
            ).apply {
                this.books.forEach { book -> book.library = this }
            }
        ).id

        entityManager.flush()
        entityManager.clear()

        val library = libraryRepository.findById(libraryId).orElseThrow()
        assertThat(library.books.remove(library.books.first())).isTrue

    }
}
