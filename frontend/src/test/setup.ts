import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach, vi } from 'vitest'

// Vitest không bật globals nên Testing Library không tự dọn DOM giữa các test
afterEach(() => cleanup())

// jsdom không cài đặt cuộn trang
window.scrollTo = vi.fn() as unknown as typeof window.scrollTo
Element.prototype.scrollIntoView = vi.fn()
