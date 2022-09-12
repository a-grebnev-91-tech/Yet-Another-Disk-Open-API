package grebnev.yadoa.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static grebnev.yadoa.model.SystemItemType.*;
import static org.junit.jupiter.api.Assertions.*;

class SystemItemTest {
    Random rnd = new Random();

    @Test
    void test1_shouldCheckEqualityByIdOnly() {
        SystemItem first = new SystemItem("id", FILE);
        first.setDate(getRandomDateInPast());
        first.setUrl("test");
        first.setSize(10L);
        SystemItem second = new SystemItem("id", FOLDER);

        assertEquals(first, second);

        SystemItem third = new SystemItem("diff", FILE);
        third.setSize(first.getSize());
        third.setDate(first.getDate());
        third.setUrl(first.getUrl());

        assertNotEquals(first, third);
    }

    @Test
    void test2_shouldReturnMaxDateFromChildren() {
        List<SystemItem> items = getFourItemsWithRandomDate();

        items.get(2).setDate(LocalDateTime.now());

        LocalDateTime maxDate = items.stream().map(SystemItem::getDate).max(LocalDateTime::compareTo).get();
        items.get(1).addChild(items.get(3));
        items.get(0).addChild(items.get(1));
        items.get(0).addChild(items.get(2));

        assertEquals(maxDate, items.get(0).getDate());
        assertTrue(maxDate.isAfter(items.get(1).getDate()));
        assertTrue(maxDate.isAfter(items.get(3).getDate()));
    }

    @Test
    void test3_shouldNotAddChildToAFile() {
        SystemItem file = getSingleItem(FILE);
        SystemItem file1 = getSingleItem(FILE);
        file.addChild(file1);

        assertNull(file.getChildren());
    }

    @Test
    void test4_shouldNotSetFileAsParent() {
        SystemItem file = getSingleItem(FILE);
        SystemItem folder = getSingleItem(FOLDER);
        folder.setParent(file);

        assertNull(folder.getParent());
    }

    @Test
    void test5_shouldReplaceUpdatedChild() {
        SystemItem folder = getSingleItem(FOLDER);
        SystemItem file = new SystemItem("id", FILE);
        SystemItem fileCopy = new SystemItem("id", FILE);
        fileCopy.setSize(100L);
        folder.addChild(file);
        folder.addChild(fileCopy);

        assertEquals(1, folder.getChildren().size());
        assertEquals(fileCopy.getSize(), folder.getChildren().get("id").getSize());
    }

    @Test
    void test6_shouldNotAddNullChildren() {
        SystemItem folder = getSingleItem(FOLDER);
        assertDoesNotThrow(() -> folder.addChild(null));
        assertEquals(0, folder.getChildren().size());
    }

    //TODO remove
//    @Test
//    void test4_shouldSetParentWhenAddingToChild() {
//        SystemItem folder = getSingleItem(FOLDER);
//        SystemItem file = getSingleItem(FILE);
//
//        folder.addChild(file);
//
//        assertNotNull(file.getParent());
//        assertEquals(folder, file.getParent());
//    }
//
//    @Test
//    void test5_shouldAddChildrenWhenSettingParent() {
//        SystemItem folder = getSingleItem(FOLDER);
//        SystemItem file = getSingleItem(FILE);
//
//        file.setParent(folder);
//        assertNotNull(file.getParent());
//        assertNotNull(folder.getChildren());
//        assertFalse(folder.getChildren().isEmpty());
//        assertEquals(folder.getChildren().get(0), file);
//    }

    private List<SystemItem> getFourItemsWithRandomDate() {
        List<SystemItem> items = new ArrayList<>();
        SystemItem root = getSingleItem(FOLDER);
        SystemItem firstLevelChildFolder = getSingleItem(FOLDER);
        SystemItem firstLevelChildFile = getSingleItem(FILE);
        SystemItem secondLevelChildFile = getSingleItem(FILE);

        items.add(root);
        items.add(firstLevelChildFolder);
        items.add(firstLevelChildFile);
        items.add(secondLevelChildFile);

        return items;
    }

    private SystemItem getSingleItem(SystemItemType type) {
        SystemItem item = new SystemItem("id" + rnd.nextInt(1000), type);
        item.setDate(getRandomDateInPast());
        return item;
    }

    private LocalDateTime getRandomDateInPast() {
        LocalDateTime date = LocalDateTime.now();
        return date.minusSeconds(rnd.nextInt(1_000_000));
    }
}