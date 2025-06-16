import { defineStore } from 'pinia'
import * as feedbackApi from '@/api/feedback'

export const useFeedbackStore = defineStore('feedback', {
  state: () => ({
    list: [],
    total: 0,
    currentPage: 1,
    pageSize: 10
  }),
  actions: {
    async fetchFeedbackList(pageNum = 1, pageSize = 10) {
      const res = await feedbackApi.fetchFeedbackPage({ pageNum, pageSize })
      this.list = res.data.list || []
      this.total = res.data.total || 0
      this.currentPage = pageNum
      this.pageSize = pageSize
      return { list: this.list, total: this.total }
    },
    async addFeedback(feedback) {
      await feedbackApi.addFeedback(feedback)
      await this.fetchFeedbackList(this.currentPage, this.pageSize)
    },
    async deleteFeedback(id) {
      await feedbackApi.deleteFeedback(id)
      await this.fetchFeedbackList(this.currentPage, this.pageSize)
    }
  },
  getters: {
    feedbackList: (state) => state.list,
    feedbackTotal: (state) => state.total
  }
})
