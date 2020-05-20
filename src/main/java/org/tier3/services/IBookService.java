package org.tier3.services;

import java.util.List;
import org.tier3.entities.Book;

public interface IBookService {

    List<Book> findAllBooks();

    Book findById(long id);

    boolean isBookExist(Book book);

    void saveBook(Book book);

    void updateBook(Book book);

    void deleteBookById(long id);

}
