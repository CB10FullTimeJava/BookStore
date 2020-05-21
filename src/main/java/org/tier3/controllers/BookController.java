package org.tier3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.tier3.entities.Book;
import org.tier3.services.BookServiceImpl;

@RestController
@RequestMapping("/")
public class BookController {

    @Autowired
    BookServiceImpl bookService;

    /**
     * GET /books : Get all books.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of books in
     * the body, or with status 204 (NO CONTENT) if there are no books in
     * database.
     */
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.findAllBooks();
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * GET /books/:id : Get book with id.
     *
     * @param id the id of the book to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the book,
     * or with status 404 (NOT FOUND) if the book does not exist.
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") long id) {
        Book book = bookService.findById(id);
        if (book == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    /**
     * POST /books : Create a new book.
     *
     * @param book the book to create
     * @return the ResponseEntity with status 201 (Created) and with location
     * header the book uri, or with status 409 (Conflict) if the book already
     * exists.
     */
    @PostMapping("/books")
    public ResponseEntity<String> createBook(@RequestBody Book book, UriComponentsBuilder ucBuilder) {

        if (bookService.isBookExist(book)) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        bookService.saveBook(book);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/books/{id}").buildAndExpand(book.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * PUT /books:id : Update an existing book with id.
     *
     * @param id the id of the book to update
     * @return the ResponseEntity with state 200 (OK) and and with location
     * header the updated book uri, or with status 404 (Not found) if the book
     * does not exist.
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<String> updateBook(@PathVariable("id") long id, @RequestBody Book book, UriComponentsBuilder ucBuilder) {

        Book currentBook = bookService.findById(id);

        if (currentBook == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
//TODO update all variables of book when entity is created
        currentBook.setTitle(book.getTitle());

        bookService.updateBook(currentBook);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/books/{id}").buildAndExpand(book.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    /**
     * DELETE /books/:id : Delete book with id.
     *
     * @param id the id of the book to delete
     * @return the ResponseEntity with status 200 (OK) or status 404 (not found)
     * if book does not exist.
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable("id") long id) {

        Book book = bookService.findById(id);
        if (book == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        bookService.deleteBookById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
