import Box from '@mui/material/Box'
import { useEffect } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import ChatbotButton from '../components/General/ChatbotButton'
import Footer from '../components/General/Footer'
import NavBar from '../components/General/NavBar'

/** Trang công khai + luồng đặt khám + khu vực bệnh nhân: header, nội dung, footer, nút chatbot. */
export default function MainLayout() {
  const { pathname, hash } = useLocation()

  useEffect(() => {
    if (hash) {
      document.getElementById(hash.slice(1))?.scrollIntoView({ behavior: 'smooth' })
    } else {
      window.scrollTo({ top: 0 })
    }
  }, [pathname, hash])

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <NavBar />
      <Box component="main" sx={{ flex: 1 }}>
        <Outlet />
      </Box>
      <Footer />
      <ChatbotButton />
    </Box>
  )
}
