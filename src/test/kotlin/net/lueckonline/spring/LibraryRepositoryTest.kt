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

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Test
    fun `can save and find library with books and subsidiaries`() {
        val libraryId = libraryRepository.save(
            Library(
                name = "Central Library",
            ).apply {
                this.books.addAll(
                    setOf(
                        Book(title = "Book One", genre = Genre.BIOGRAPHY),
                        Book(title = "Book Two", genre = Genre.NON_FICTION),
                        Book(title = "Book Three", genre = Genre.FICTION)
                    )
                )
                this.books.forEach { book -> book.library = this}
                this.subsidiaries.addAll(
                    setOf(
                        Subsidiary(name = "Subsidiary 1", address = "Address 1"),
                        Subsidiary(name = "Subsidiary 2", address = "Address 2"),
                        Subsidiary(name = "Subsidiary 3", address = "Address 3"),
                        Subsidiary(name = "Subsidiary 4", address = "Address 4")
                    )
                )
                this.subsidiaries.forEach { subsidiary -> subsidiary.library = this }
            }
        ).id

        entityManager.flush()
        entityManager.clear()

        libraryRepository.findById(libraryId).orElseThrow().let { it ->
            assertThat(it.books.remove(it.books.find { it.title == "Book Two" })).isTrue
        }

        entityManager.flush()
        entityManager.clear()

        val removed = bookRepository.findByTitleAndGenre(title = "Book One", genre = Genre.BIOGRAPHY).first().let {
            it.library!!.books.remove(it)
        }
        assertThat(removed).isTrue

    }
}
