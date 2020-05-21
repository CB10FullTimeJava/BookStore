package org.tier3.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;
import org.tier3.entities.Book;

@Service("bookService")
public class BookServiceImpl implements IBookService {

    private static final AtomicLong counter = new AtomicLong();

    private static List<Book> books;

    static {
        books = populateDummyBooks();
    }

    public List<Book> findAllBooks() {
        return books;
    }

    private static List<Book> populateDummyBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Withering Heights"));
        books.add(new Book(2, "Sapiens"));
        return books;
    }

    @Override
    public Book findById(long id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    @Override
    public boolean isBookExist(Book book) {
//        TODO book exists only when id is the same?
        return findById(book.getId()) != null;

    }

    @Override
    public void saveBook(Book book) {
//        get correct ID from DB
        book.setId(counter.incrementAndGet());
        books.add(book);
    }

    @Override
    public void updateBook(Book book) {
        int index = books.indexOf(book);
        books.set(index, book);
    }

    @Override
    public void deleteBookById(long id) {
        for (Iterator<Book> iterator = books.iterator(); iterator.hasNext();) {
            Book user = iterator.next();
            if (user.getId() == id) {
                iterator.remove();
            }
        }
    }

}
