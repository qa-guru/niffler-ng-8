package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String categoryName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        categoryName,
        username,
        true
    );

    InvalidCategoryNameException ex = assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + categoryName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

  @Test
  void getAllCategoriesIsFilteredByExcludeArchived(@Mock CategoryRepository categoryRepository) {
    final String username = "timofey";
    final CategoryEntity firstCategory = new CategoryEntity();
    firstCategory.setId(UUID.randomUUID());
    firstCategory.setUsername(username);
    firstCategory.setName("Name");
    firstCategory.setArchived(false);

    final CategoryEntity secondCategory = new CategoryEntity();
    secondCategory.setId(UUID.randomUUID());
    secondCategory.setUsername(username);
    secondCategory.setName("SecondName");
    secondCategory.setArchived(false);

    final CategoryEntity thirdCategory = new CategoryEntity();
    thirdCategory.setId(UUID.randomUUID());
    thirdCategory.setUsername(username);
    thirdCategory.setName("ThirdName");
    thirdCategory.setArchived(true);

    when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
        .thenReturn(List.of(firstCategory, secondCategory, thirdCategory));

    CategoryService categoryService = new CategoryService(categoryRepository);
    List<CategoryJson> result = categoryService.getAllCategories(username, true);

    assertEquals(2, result.size());
    assertFalse(result.getFirst().archived());
    assertFalse(result.getLast().archived());
  }

  @Test
  void updateCategoryShouldThrowTooManyCategoriesExceptionIfMaxCategoriesSizeIsExceeded(@Mock CategoryRepository categoryRepository) {
    final String username = "timofey";
    final UUID catId = UUID.randomUUID();
    final String catName = "Name";
    final long count = 8;

    final CategoryEntity category = new CategoryEntity();
    category.setId(catId);
    category.setUsername(username);
    category.setName(catName);
    category.setArchived(true);

    final CategoryJson catJson = new CategoryJson(
        catId,
        catName ,
        username,
        false
    );

    when(categoryRepository.findByUsernameAndId(eq(username), eq(catId)))
        .thenReturn(Optional.of(category));
    when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
        .thenReturn(count);

    CategoryService categoryService = new CategoryService(categoryRepository);

    final TooManyCategoriesException exception = assertThrows(TooManyCategoriesException.class,
        () -> categoryService.update(catJson));
    assertEquals(
        "Can`t unarchive category for user: '" + username + "'",
        exception.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void saveShouldThrowExceptionInCaseOfArchivedCategoryName(String categoryName, @Mock CategoryRepository categoryRepository) {
    final String username = "timofey";
    final UUID id = UUID.randomUUID();

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        categoryName,
        username,
        true
    );

    InvalidCategoryNameException ex = assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.save(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + categoryName + "'",
        ex.getMessage()
    );
  }

  @Test
  void saveShouldThrowExceptionInCaseOfMaxCategoriesSizeIsExceeded(@Mock CategoryRepository categoryRepository) {
    final String username = "timofey";
    final UUID catId = UUID.randomUUID();
    final String categoryName = "Name";
    final long count = 8;

    final CategoryJson categoryJson = new CategoryJson(
        catId,
        categoryName,
        username,
        false
    );

    when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
        .thenReturn(count);

    CategoryService categoryService = new CategoryService(categoryRepository);

    final TooManyCategoriesException exception = assertThrows(TooManyCategoriesException.class,
        () -> categoryService.save(categoryJson));
    assertEquals(
        "Can`t add over than 8 categories for user: '" + username + "'",
        exception.getMessage()
    );
  }
}