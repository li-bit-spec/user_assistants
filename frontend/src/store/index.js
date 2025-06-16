import { createPinia } from 'pinia'
import { useManualStore } from './modules/manualStore'
import { useFeedbackStore } from './modules/feedbackStore'
import { useSupportStore } from './modules/supportStore'

const pinia = createPinia()

export default pinia
export { useManualStore, useFeedbackStore, useSupportStore }
