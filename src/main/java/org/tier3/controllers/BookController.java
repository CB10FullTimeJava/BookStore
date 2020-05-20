package org.tier3.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.tier3.entities.Book;
import org.tier3.services.BookServiceImpl;
import org.tier3.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class BookController {

    public static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookServiceImpl bookService;

    // -------------------Retrieve All Books---------------------------------------------
    @RequestMapping(value = "/book/", method = RequestMethod.GET)
    public ResponseEntity<List<Book>> listAllUsers() {
        List<Book> books = bookService.findAllBooks();
        if (books.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Book>>(books, HttpStatus.OK);
    }

    // -------------------Retrieve Single Book------------------------------------------
    @RequestMapping(value = "/book/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getBook(@PathVariable("id") long id) {
        logger.info("Fetching book with id {}", id);
        Book book = bookService.findById(id);
        if (book == null) {
            logger.error("Book with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Book with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }

    // -------------------Create a Book-------------------------------------------
    @RequestMapping(value = "/book/", method = RequestMethod.POST)
    public ResponseEntity<?> createBook(@RequestBody Book book, UriComponentsBuilder ucBuilder) {
        logger.info("Creating book : {}", book);

        if (bookService.isBookExist(book)) {
            logger.error("Unable to create. A book with title {} already exist", book.getTitle());
            return new ResponseEntity(new CustomErrorType("Unable to create. A book with title "
                    + book.getTitle() + " already exist."), HttpStatus.CONFLICT);
        }
        bookService.saveBook(book);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/book/{id}").buildAndExpand(book.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

//    // ------------------- Update a Book ------------------------------------------------
    @RequestMapping(value = "/book/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBook(@PathVariable("id") long id, @RequestBody Book book) {
        logger.info("Updating Book with id {}", id);

        Book currentBook = bookService.findById(id);

        if (currentBook == null) {
            logger.error("Unable to update. Book with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Book with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        currentBook.setTitle(book.getTitle());

        bookService.updateBook(currentBook);
        return new ResponseEntity<Book>(currentBook, HttpStatus.OK);
    }

    // ------------------- Delete a Book-----------------------------------------
    @RequestMapping(value = "/book/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting book with id {}", id);

        Book book = bookService.findById(id);
        if (book == null) {
            logger.error("Unable to delete. Book with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Book with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        bookService.deleteBookById(id);
        return new ResponseEntity<Book>(HttpStatus.NO_CONTENT);
    }
}
