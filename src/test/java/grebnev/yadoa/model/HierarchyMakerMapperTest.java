package grebnev.yadoa.model;

import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.repository.SystemItemRepository;
import grebnev.yadoa.mapper.HierarchyMakerMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static grebnev.yadoa.model.SystemItemType.FILE;
import static grebnev.yadoa.model.SystemItemType.FOLDER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HierarchyMakerMapperTest {
    private static final String ROOT_FOLDER_ID = "rootLevelFolder";

    private static final String FIRST_LEVEL_FILE_ID = "firstLevelFile";
    private static final String FIRST_LEVEL_FILE_URL = "first/level/url";

    private static final String FIRST_LEVEL_FOLDER_ID = "firstLevelFolder";

    private static final String FIRST_LEVEL_EMPTY_FOLDER_ID = "firstLevelEmptyFolder";

    private static final String SECOND_LEVEL_FILE_ID = "secondLevelFile";
    private static final String SECOND_LEVEL_FILE_URL = "second/level/url";

    private static final String SECOND_LEVEL_FOLDER_ID = "secondLevelFolder";

    private static final String THIRD_LEVEL_FILE_ID = "thirdLevelFile";
    private static final String THIRD_LEVEL_FILE_URL = "third/level/url";


    private HierarchyMakerMapper mapper;
    private Random rnd = new Random();
    private Instant latestDate;
    private Instant rootDate;
    private Instant firstLevelFileDate;
    private Instant firstLevelFolderDate;
    private Instant firstLevelEmptyFolderDate;
    private Instant secondLevelFileDate;
    private Instant secondLevelFolderDate;
    private Instant thirdLevelFileDate;

    @BeforeAll
    void initMapper() {
        mapper = new HierarchyMakerMapper();
    }

    @Test
    void test1_shouldThrowExceptionForNullList() {
        assertThrows(IllegalArgumentException.class, () -> mapper.getHierarchy(null));
    }

    @Test
    void test1_shouldThrowNotFoundExceptionForEmptyList() {
        assertThrows(NotFoundException.class, () -> mapper.getHierarchy(Collections.emptyList()));
    }

    @Test
    void test1_shouldCreateCurrentHierarchy() {
        SystemItem root = mapper.getHierarchy(getSixLeveledItemsWithLatestUpdateDateOn2ndLvlFolder());

        Map<String, SystemItem> firstLevelChildren = root.getChildren();

        assertEquals(ROOT_FOLDER_ID, root.getId());
        assertEquals(latestDate, root.getDate());
        assertEquals(FOLDER, root.getType());
        assertEquals(3, root.getSize());
        assertEquals(3, root.getChildren().size());

        SystemItem firstLevelFile = firstLevelChildren.get(FIRST_LEVEL_FILE_ID);
        assertNotNull(firstLevelFile);
        assertNull(firstLevelFile.getChildren());
        assertEquals(1, firstLevelFile.getSize());
        assertEquals(FIRST_LEVEL_FILE_URL, firstLevelFile.getUrl());
        assertEquals(firstLevelFileDate, firstLevelFile.getDate());

        SystemItem firstLevelFolder = firstLevelChildren.get(FIRST_LEVEL_FOLDER_ID);
        assertNotNull(firstLevelFolder);
        assertNotNull(firstLevelFolder.getChildren());
        assertEquals(2, firstLevelFolder.getChildren().size());
        assertEquals(2, firstLevelFolder.getSize());
        assertEquals(latestDate, firstLevelFolder.getDate());

        SystemItem firstLevelEmptyFolder = firstLevelChildren.get(FIRST_LEVEL_EMPTY_FOLDER_ID);
        assertNotNull(firstLevelEmptyFolder);
        assertNotNull(firstLevelEmptyFolder.getChildren());
        assertEquals(0, firstLevelEmptyFolder.getChildren().size());
        assertEquals(0, firstLevelEmptyFolder.getSize());
        assertEquals(firstLevelEmptyFolderDate, firstLevelEmptyFolder.getDate());


        Map<String, SystemItem> secondLevelChildren = firstLevelFolder.getChildren();

        SystemItem secondLevelFile = secondLevelChildren.get(SECOND_LEVEL_FILE_ID);
        assertNotNull(secondLevelFile);
        assertNull(secondLevelFile.getChildren());
        assertEquals(1, secondLevelFile.getSize());
        assertEquals(SECOND_LEVEL_FILE_URL, secondLevelFile.getUrl());
        assertEquals(secondLevelFileDate, secondLevelFile.getDate());

        SystemItem secondLevelFolder = secondLevelChildren.get(SECOND_LEVEL_FOLDER_ID);
        assertNotNull(secondLevelFolder);
        assertNotNull(secondLevelFolder.getChildren());
        assertEquals(1, secondLevelFolder.getChildren().size());
        assertEquals(1, secondLevelFolder.getSize());
        assertEquals(latestDate, secondLevelFolder.getDate());

        Map<String, SystemItem> thirdLevelChildren = secondLevelFolder.getChildren();

        SystemItem thirdLevelFile = thirdLevelChildren.get(THIRD_LEVEL_FILE_ID);
        assertNotNull(thirdLevelFile);
        assertNull(thirdLevelFile.getChildren());
        assertEquals(1, thirdLevelFile.getSize());
        assertEquals(THIRD_LEVEL_FILE_URL, thirdLevelFile.getUrl());
        assertEquals(thirdLevelFileDate, thirdLevelFile.getDate());
    }

    private List<SystemItemRepository.LeveledSystemItemEntity> getSixLeveledItemsWithLatestUpdateDateOn2ndLvlFolder() {
        List<SystemItemRepository.LeveledSystemItemEntity> items = new ArrayList<>();

        SystemItemRepository.LeveledSystemItemEntity rootFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(rootFolder.getId()).thenReturn(ROOT_FOLDER_ID);
        when(rootFolder.getLevel()).thenReturn(0);
        when((rootFolder.getType())).thenReturn(FOLDER);
        rootDate = getRandomDateInPast();
        when(rootFolder.getUpdated()).thenReturn(rootDate);

        SystemItemRepository.LeveledSystemItemEntity firstLevelFile
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(firstLevelFile.getId()).thenReturn(FIRST_LEVEL_FILE_ID);
        when(firstLevelFile.getUrl()).thenReturn(FIRST_LEVEL_FILE_URL);
        firstLevelFileDate = getRandomDateInPast();
        when(firstLevelFile.getUpdated()).thenReturn(firstLevelFileDate);
        when(firstLevelFile.getParent()).thenReturn(ROOT_FOLDER_ID);
        when((firstLevelFile.getType())).thenReturn(FILE);
        when(firstLevelFile.getSize()).thenReturn(1L);
        when(firstLevelFile.getLevel()).thenReturn(1);

        SystemItemRepository.LeveledSystemItemEntity firstLevelFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(firstLevelFolder.getId()).thenReturn(FIRST_LEVEL_FOLDER_ID);
        firstLevelFolderDate = getRandomDateInPast();
        when(firstLevelFolder.getUpdated()).thenReturn(firstLevelFolderDate);
        when(firstLevelFolder.getParent()).thenReturn(ROOT_FOLDER_ID);
        when((firstLevelFolder.getType())).thenReturn(FOLDER);
        when(firstLevelFolder.getLevel()).thenReturn(1);

        SystemItemRepository.LeveledSystemItemEntity firstLevelEmptyFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(firstLevelEmptyFolder.getId()).thenReturn(FIRST_LEVEL_EMPTY_FOLDER_ID);
        firstLevelEmptyFolderDate = getRandomDateInPast();
        when(firstLevelEmptyFolder.getUpdated()).thenReturn(firstLevelEmptyFolderDate);
        when(firstLevelEmptyFolder.getParent()).thenReturn(ROOT_FOLDER_ID);
        when((firstLevelEmptyFolder.getType())).thenReturn(FOLDER);
        when(firstLevelEmptyFolder.getLevel()).thenReturn(1);

        SystemItemRepository.LeveledSystemItemEntity secondLevelFile
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(secondLevelFile.getId()).thenReturn(SECOND_LEVEL_FILE_ID);
        when(secondLevelFile.getUrl()).thenReturn(SECOND_LEVEL_FILE_URL);
        secondLevelFileDate = getRandomDateInPast();
        when(secondLevelFile.getUpdated()).thenReturn(secondLevelFileDate);
        when(secondLevelFile.getParent()).thenReturn(FIRST_LEVEL_FOLDER_ID);
        when((secondLevelFile.getType())).thenReturn(FILE);
        when(secondLevelFile.getSize()).thenReturn(1L);
        when(secondLevelFile.getLevel()).thenReturn(2);

        SystemItemRepository.LeveledSystemItemEntity secondLevelFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        secondLevelFolderDate = Instant.now();
        latestDate = secondLevelFolderDate;
        when(secondLevelFolder.getId()).thenReturn(SECOND_LEVEL_FOLDER_ID);
        when(secondLevelFolder.getUpdated()).thenReturn(secondLevelFolderDate);
        when(secondLevelFolder.getParent()).thenReturn(FIRST_LEVEL_FOLDER_ID);
        when((secondLevelFolder.getType())).thenReturn(FOLDER);
        when(secondLevelFolder.getLevel()).thenReturn(2);

        SystemItemRepository.LeveledSystemItemEntity thirdLevelFile
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(thirdLevelFile.getId()).thenReturn(THIRD_LEVEL_FILE_ID);
        when(thirdLevelFile.getUrl()).thenReturn(THIRD_LEVEL_FILE_URL);
        thirdLevelFileDate = getRandomDateInPast();
        when(thirdLevelFile.getUpdated()).thenReturn(thirdLevelFileDate);
        when((thirdLevelFile.getType())).thenReturn(FILE);
        when(thirdLevelFile.getParent()).thenReturn(SECOND_LEVEL_FOLDER_ID);
        when(thirdLevelFile.getSize()).thenReturn(1L);
        when(thirdLevelFile.getLevel()).thenReturn(3);

        items.add(rootFolder);
        items.add(firstLevelFolder);
        items.add(firstLevelFile);
        items.add(firstLevelEmptyFolder);
        items.add(secondLevelFile);
        items.add(secondLevelFolder);
        items.add(thirdLevelFile);

        return items;
    }

    private Instant getRandomDateInPast() {
        Instant date = Instant.now();
        return date.minusSeconds(rnd.nextInt(1_000_000));
    }
}