package net.lueckonline.spring

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BookRepository : JpaRepository<Book, UUID>

