import { defineStore } from 'pinia'
import * as supportApi from '@/api/support'

export const useSupportStore = defineStore('support', {
  state: () => ({
    list: [],
    currentSupport: null
  }),
  actions: {
    async fetchSupportList() {
      const res = await supportApi.fetchSupportList()
      this.list = res.data || []
      return this.list
    },
    async fetchSupportById(id) {
      const res = await supportApi.fetchSupportById(id)
      this.currentSupport = res.data
      return this.currentSupport
    },
    async addSupport(support) {
      await supportApi.addSupport(support)
      await this.fetchSupportList()
    },
    async updateSupport(support) {
      await supportApi.updateSupport(support)
      await this.fetchSupportList()
    },
    async deleteSupport(id) {
      await supportApi.deleteSupport(id)
      await this.fetchSupportList()
    }
  },
  getters: {
    supportList: (state) => state.list,
    supportById: (state) => (id) => state.list.find(s => s.id === id)
  }
})
