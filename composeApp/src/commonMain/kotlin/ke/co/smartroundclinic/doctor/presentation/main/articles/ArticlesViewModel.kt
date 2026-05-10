package ke.co.smartroundclinic.doctor.presentation.main.articles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.domain.repository.ArticleCategoryLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.articles.CreateArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.DeleteArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetCategoriesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetLiveArticlesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetMyArticlesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.PublishArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.UnpublishArticleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.UpdateArticleUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticlesViewModel(
    private val getMyArticlesUseCase: GetMyArticlesUseCase,
    private val getLiveArticlesUseCase: GetLiveArticlesUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createArticleUseCase: CreateArticleUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase,
    private val publishArticleUseCase: PublishArticleUseCase,
    private val unpublishArticleUseCase: UnpublishArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val articleLocalRepository: ArticleLocalRepository,
    private val categoryLocalRepository: ArticleCategoryLocalRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val myArticles: StateFlow<List<Article>> = articleLocalRepository.observeMyArticles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val liveArticles: StateFlow<List<Article>> = articleLocalRepository.observeLiveArticles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<ArticleCategory>> = categoryLocalRepository.observeActiveCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var isLoadingMine by mutableStateOf(false)
        private set

    var isLoadingLive by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var thumbnailBytes by mutableStateOf<ByteArray?>(null)
        private set

    var thumbnailFilename by mutableStateOf<String?>(null)
        private set

    init {
        refreshMyArticles()
        viewModelScope.launch { getCategoriesUseCase() }
    }

    fun refreshMyArticles() {
        viewModelScope.launch {
            isLoadingMine = true
            getMyArticlesUseCase()
            isLoadingMine = false
        }
    }

    fun refreshLiveArticles() {
        viewModelScope.launch {
            isLoadingLive = true
            getLiveArticlesUseCase()
            isLoadingLive = false
        }
    }

    fun setThumbnail(bytes: ByteArray?, filename: String?) {
        thumbnailBytes = bytes
        thumbnailFilename = filename
    }

    fun clearThumbnail() {
        thumbnailBytes = null
        thumbnailFilename = null
    }

    fun createArticle(
        title: String,
        content: String,
        summary: String,
        categoryId: String,
        onSuccess: (Article) -> Unit,
    ) {
        viewModelScope.launch {
            isSaving = true
            val result = createArticleUseCase(title, content, summary, categoryId, thumbnailBytes, thumbnailFilename)
            isSaving = false
            when (result) {
                is Resource.Success -> {
                    clearThumbnail()
                    result.data?.let { onSuccess(it) }
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to create article")
                else -> Unit
            }
        }
    }

    fun updateArticle(
        id: String,
        title: String,
        content: String,
        summary: String,
        categoryId: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            isSaving = true
            val result = updateArticleUseCase(id, title, content, summary, categoryId, thumbnailBytes, thumbnailFilename)
            isSaving = false
            when (result) {
                is Resource.Success -> {
                    clearThumbnail()
                    onSuccess()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to update article")
                else -> Unit
            }
        }
    }

    fun publishArticle(id: String) {
        viewModelScope.launch {
            val result = publishArticleUseCase(id)
            if (result is Resource.Error) snackbarController.show(result.message ?: "Failed to publish")
            else snackbarController.show("Article is now live")
        }
    }

    fun unpublishArticle(id: String) {
        viewModelScope.launch {
            val result = unpublishArticleUseCase(id)
            if (result is Resource.Error) snackbarController.show(result.message ?: "Failed to unpublish")
            else snackbarController.show("Article moved back to draft")
        }
    }

    fun deleteArticle(id: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = deleteArticleUseCase(id)
            if (result is Resource.Error) snackbarController.show(result.message ?: "Failed to delete article")
            else {
                snackbarController.show("Article deleted")
                onSuccess()
            }
        }
    }
}
