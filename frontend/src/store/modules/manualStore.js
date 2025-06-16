import { defineStore } from 'pinia'
import * as manualApi from '@/api/manual'

export const useManualStore = defineStore('manual', {
  state: () => ({
    articles: [],
    loading: false,
    showNewArticleDialog: false,
    currentArticle: null
  }),
  actions: {
    async fetchArticles() {
      this.loading = true
      try {
        const res = await manualApi.fetchManualList()
        this.articles = res.data || []
        return this.articles
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },
    async fetchArticleById(id) {
      const res = await manualApi.fetchManualById(id)
      this.currentArticle = res.data
      return this.currentArticle
    },
    async addArticle(article) {
      await manualApi.addManual(article)
      await this.fetchArticles()
    },
    async updateArticle(article) {
      await manualApi.updateManual(article)
      await this.fetchArticles()
    },
    async deleteArticle(id) {
      await manualApi.deleteManual(id)
      await this.fetchArticles()
    },
    setShowNewArticleDialog(show) {
      this.showNewArticleDialog = show
    }
  },
  getters: {
    articleList: (state) => state.articles,
    articleById: (state) => (id) => state.articles.find(a => a.id === id)
  }
})
