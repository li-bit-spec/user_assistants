import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'
import ManualView from '@/views/ManualView.vue'
import FeedbackView from '@/views/FeedbackView.vue'
import SupportView from '@/views/SupportView.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/manual',
    children: [
      {
        path: 'manual',
        name: 'Manual',
        component: ManualView
      },
      {
        path: 'manual/:id',
        name: 'ManualDetail',
        component: ManualView
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: FeedbackView
      },
      {
        path: 'support',
        name: 'Support',
        component: SupportView
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
