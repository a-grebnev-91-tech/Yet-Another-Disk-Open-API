package grebnev.yadoa.model;

import grebnev.yadoa.repository.SystemItemRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static grebnev.yadoa.model.SystemItemType.FILE;
import static grebnev.yadoa.model.SystemItemType.FOLDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HierarchyMakerMapperTest {
    private static final String ROOT_FOLDER_ID = "rootLevelFolder";
    private static final String FIRST_LEVEL_FILE_ID = "firstLevelFile";
    private static final String FIRST_LEVEL_FILE_URL = "first/level/url";
    private static final String FIRST_LEVEL_FOLDER_1_ID = "firstLevelFolder";
    private static final String FIRST_LEVEL_FOLDER_2_ID = "firstLevelFolder";
    private static final String SECOND_LEVEL_FILE_ID = "secondLevelFile";
    private static final String SECOND_LEVEL_FILE_URL = "second/level/url";
    private static final String SECOND_LEVEL_FOLDER_ID = "secondLevelFolder";
    private static final String THIRD_LEVEL_FILE_ID = "thirdLevelFile";
    private static final String THIRD_LEVEL_FILE_URL = "third/level/url";

    HierarchyMakerMapper mapper;
    Random rnd = new Random();
    LocalDateTime latestDate;

    @BeforeAll
    void initMapper() {
        mapper = new HierarchyMakerMapper();
    }

    @Test
    void test1_shouldCreateCurrentHierarchy() {
        SystemItem root = mapper.getHierarchy(getSixLeveledItemsWithLatestUpdateDateOn2ndLvlFolder());
        List<SystemItem> firstLevelChildren = root.getChildren();

        assertEquals(ROOT_FOLDER_ID, root.getId());
        assertEquals(latestDate, root.getDate());
        assertEquals(FOLDER, root.getType());
        assertEquals(3, root.getSize());
        assertEquals(2, root.getChildren().size());
    }

    private List<SystemItemRepository.LeveledSystemItemEntity> getSixLeveledItemsWithLatestUpdateDateOn2ndLvlFolder() {
        List<SystemItemRepository.LeveledSystemItemEntity> items = new ArrayList<>();

        SystemItemRepository.LeveledSystemItemEntity rootFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        when(rootFolder.getId()).thenReturn(ROOT_FOLDER_ID);
        when(rootFolder.getLevel()).thenReturn(0);
        when((rootFolder.getType())).thenReturn(FOLDER);
        LocalDateTime rootDate = getRandomDateInPast();
        when(rootFolder.getUpdateDate()).thenReturn(rootDate);

        SystemItemRepository.LeveledSystemItemEntity firstLevelFile
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        LocalDateTime firstLevelFileDate = getRandomDateInPast();
        when(firstLevelFile.getId()).thenReturn(FIRST_LEVEL_FILE_ID);
        when(firstLevelFile.getUrl()).thenReturn(FIRST_LEVEL_FILE_URL);
        when(firstLevelFile.getUpdateDate()).thenReturn(firstLevelFileDate);
        when(firstLevelFile.getParentId()).thenReturn(ROOT_FOLDER_ID);
        when((firstLevelFile.getType())).thenReturn(FILE);
        when(firstLevelFile.getSize()).thenReturn(1L);
        when(firstLevelFile.getLevel()).thenReturn(1);

        SystemItemRepository.LeveledSystemItemEntity firstLevelFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        LocalDateTime firstLevelFolderDate = getRandomDateInPast();
        when(firstLevelFolder.getId()).thenReturn(FIRST_LEVEL_FOLDER_1_ID);
        when(firstLevelFolder.getUpdateDate()).thenReturn(firstLevelFolderDate);
        when(firstLevelFolder.getParentId()).thenReturn(ROOT_FOLDER_ID);
        when((firstLevelFolder.getType())).thenReturn(FOLDER);
        when(firstLevelFolder.getLevel()).thenReturn(1);

        SystemItemRepository.LeveledSystemItemEntity firstLevelFolder1
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        LocalDateTime firstLevelFolder1Date = getRandomDateInPast();
        when(firstLevelFolder1.getId()).thenReturn(FIRST_LEVEL_FOLDER_2_ID);
        when(firstLevelFolder1.getUpdateDate()).thenReturn(firstLevelFolder1Date);
        when(firstLevelFolder1.getParentId()).thenReturn(ROOT_FOLDER_ID);
        when((firstLevelFolder1.getType())).thenReturn(FOLDER);
        when(firstLevelFolder1.getLevel()).thenReturn(1);

        SystemItemRepository.LeveledSystemItemEntity secondLevelFile
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        LocalDateTime secondLevelFileDate = getRandomDateInPast();
        when(secondLevelFile.getId()).thenReturn(SECOND_LEVEL_FILE_ID);
        when(secondLevelFile.getUrl()).thenReturn(SECOND_LEVEL_FILE_URL);
        when(secondLevelFile.getUpdateDate()).thenReturn(secondLevelFileDate);
        when(secondLevelFile.getParentId()).thenReturn(FIRST_LEVEL_FOLDER_1_ID);
        when((secondLevelFile.getType())).thenReturn(FILE);
        when(secondLevelFile.getSize()).thenReturn(1L);
        when(secondLevelFile.getLevel()).thenReturn(2);

        SystemItemRepository.LeveledSystemItemEntity secondLevelFolder
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        LocalDateTime secondLevelFolderDate = LocalDateTime.now();
        latestDate = secondLevelFolderDate;
        when(secondLevelFolder.getId()).thenReturn(SECOND_LEVEL_FOLDER_ID);
        when(secondLevelFolder.getUpdateDate()).thenReturn(secondLevelFolderDate);
        when(secondLevelFolder.getParentId()).thenReturn(FIRST_LEVEL_FOLDER_1_ID);
        when((secondLevelFolder.getType())).thenReturn(FOLDER);
        when(secondLevelFolder.getLevel()).thenReturn(2);

        SystemItemRepository.LeveledSystemItemEntity thirdLevelFile
                = Mockito.mock(SystemItemRepository.LeveledSystemItemEntity.class);
        LocalDateTime thirdLevelFileDate = getRandomDateInPast();
        when(thirdLevelFile.getId()).thenReturn(THIRD_LEVEL_FILE_ID);
        when(thirdLevelFile.getUrl()).thenReturn(THIRD_LEVEL_FILE_URL);
        when(thirdLevelFile.getUpdateDate()).thenReturn(thirdLevelFileDate);
        when((thirdLevelFile.getType())).thenReturn(FILE);
        when(thirdLevelFile.getParentId()).thenReturn(SECOND_LEVEL_FOLDER_ID);
        when(thirdLevelFile.getSize()).thenReturn(1L);
        when(thirdLevelFile.getLevel()).thenReturn(3);

        items.add(rootFolder);
        items.add(firstLevelFolder);
        items.add(firstLevelFile);
        items.add(secondLevelFile);
        items.add(secondLevelFolder);
        items.add(thirdLevelFile);

        return items;
    }

    private LocalDateTime getRandomDateInPast() {
        LocalDateTime date = LocalDateTime.now();
        return date.minusSeconds(rnd.nextInt(1_000_000));
    }
}