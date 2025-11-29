package com.library.management.service;

import com.library.management.dto.BookDTO;
import com.library.management.entity.Book;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        // Check if ISBN already exists
        if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .isbn(bookDTO.getIsbn())
                .category(bookDTO.getCategory())
                .totalCopies(bookDTO.getTotalCopies())
                .availableCopies(bookDTO.getTotalCopies())
                .publisher(bookDTO.getPublisher())
                .publishYear(bookDTO.getPublishYear())
                .description(bookDTO.getDescription())
                .status(Book.BookStatus.AVAILABLE)
                .build();

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToDTO(book);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getLowStockBooks() {
        return bookRepository.findLowStockBooks().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Check if ISBN is being changed and if it already exists
        if (!book.getIsbn().equals(bookDTO.getIsbn())) {
            if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
                throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
            }
        }

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setCategory(bookDTO.getCategory());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishYear(bookDTO.getPublishYear());
        book.setDescription(bookDTO.getDescription());

        // Update total copies and adjust available copies accordingly
        if (bookDTO.getTotalCopies() != null) {
            int difference = bookDTO.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(bookDTO.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + difference);
        }

        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    @Transactional
    public void decrementAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        if (book.getAvailableCopies() == 0) {
            book.setStatus(Book.BookStatus.OUT_OF_STOCK);
        }
        bookRepository.save(book);
    }

    @Transactional
    public void incrementAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        if (book.getAvailableCopies() > 0) {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }
        bookRepository.save(book);
    }

    private BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .description(book.getDescription())
                .status(book.getStatus().name())
                .build();
    }
}