package com.gcc.library1;

import com.gcc.library1.model.Book;
import com.gcc.library1.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookDataInsertTest {

    private static final String HUOZHE_ISBN = "978-7-5302-2153-2";
    private static final String BNGD_ISBN = "978-7-5442-5399-4";
    private static final String HLM_ISBN = "978-7-02-000220-7";

    @Autowired
    private BookRepository bookRepository;

    @Test
    void insertAndVerifySampleBooks() {
        // 《三体》三部曲 —— 每种1本，同一个系列不同ISBN
        bookRepository.save(createBook("978-7-5366-9293-0", "三体", "刘慈欣",
                "文化大革命如火如荼进行的同时，军方探寻外星文明的绝秘计划" +
                        "红岸工程取得了突破性进展。"));
        bookRepository.save(createBook("978-7-5366-9294-7", "三体II：黑暗森林", "刘慈欣",
                "三体人在利用魔法般的科技锁死了地球人的科学之后，庞大的宇宙舰队开始向地球进发。"));
        bookRepository.save(createBook("978-7-5366-9295-4", "三体III：死神永生", "刘慈欣",
                "与三体文明的战争使人类第一次看到了宇宙黑暗的真相。"));

        // 《活着》—— 同一ISBN，3个副本（热门书，多备几本）
        bookRepository.save(createBook(HUOZHE_ISBN, "活着", "余华",
                "地主少爷富贵嗜赌成性，终于赌光了家业一贫如洗。"));
        bookRepository.save(createBook(HUOZHE_ISBN, "活着", "余华",
                "地主少爷富贵嗜赌成性，终于赌光了家业一贫如洗。"));
        bookRepository.save(createBook(HUOZHE_ISBN, "活着", "余华",
                "地主少爷富贵嗜赌成性，终于赌光了家业一贫如洗。"));

        // 《百年孤独》—— 同一ISBN，2个副本
        bookRepository.save(createBook(BNGD_ISBN, "百年孤独", "加西亚·马尔克斯",
                "魔幻现实主义文学的代表作，描写了布恩迪亚家族七代人的传奇故事。"));
        bookRepository.save(createBook(BNGD_ISBN, "百年孤独", "加西亚·马尔克斯",
                "魔幻现实主义文学的代表作，描写了布恩迪亚家族七代人的传奇故事。"));

        // 《围城》—— 1本
        bookRepository.save(createBook("978-7-02-002475-0", "围城", "钱钟书",
                "围城故事发生于1920到1940年代，讲述主人公方鸿渐的婚姻与事业困境。"));

        // 《红楼梦》—— 同一ISBN，2个副本
        bookRepository.save(createBook(HLM_ISBN, "红楼梦", "曹雪芹",
                "以贾、史、王、薛四大家族的兴衰为背景，以富贵公子贾宝玉为视角。"));
        bookRepository.save(createBook(HLM_ISBN, "红楼梦", "曹雪芹",
                "以贾、史、王、薛四大家族的兴衰为背景，以富贵公子贾宝玉为视角。"));

        // === 验证 ===
        List<Book> all = bookRepository.findAll();
        System.out.println("\n===== 共插入 " + all.size() + " 本书 =====");

        // 总数为 3 + 3 + 2 + 1 + 2 = 11
        assertThat(all).hasSize(11);

        // 验证副本数：同一ISBN的多本书
        System.out.println("活着 副本数: " + bookRepository.countByIsbn(HUOZHE_ISBN));
        assertThat(bookRepository.countByIsbn(HUOZHE_ISBN)).isEqualTo(3);

        System.out.println("百年孤独 副本数: " + bookRepository.countByIsbn(BNGD_ISBN));
        assertThat(bookRepository.countByIsbn(BNGD_ISBN)).isEqualTo(2);

        System.out.println("红楼梦 副本数: " + bookRepository.countByIsbn(HLM_ISBN));
        assertThat(bookRepository.countByIsbn(HLM_ISBN)).isEqualTo(2);

        // 验证 findByIsbn 返回所有副本
        List<Book> huozheBooks = bookRepository.findByIsbn(HUOZHE_ISBN);
        assertThat(huozheBooks).hasSize(3);
        huozheBooks.forEach(b -> assertThat(b.getTitle()).isEqualTo("活着"));

        // 打印所有书
        System.out.println("---");
        all.forEach(b -> System.out.printf("  id=%d | ISBN=%s | %s - %s%n",
                b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor()));
    }

    private Book createBook(String isbn, String title, String author, String description) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        return book;
    }
}
