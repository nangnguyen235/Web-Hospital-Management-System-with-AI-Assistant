import { createTheme } from '@mui/material/styles'

/** Bảng màu theo cách trình bày của web TheDuck (frontend.md mục 4). */
export const MAU = {
  chinh: '#003a91',
  xanhDam: '#17496c',
  xanh: '#0b5394',
  xanhSang: '#00a9dd',
  xanhNhat: '#6fccea',
  xanhRatNhat: '#cfe2f3',
  chu: '#003553',
  nen: '#F5F7FB',
  nenDatKham: '#E8F2F7',
  xoa: '#f0857f',
} as const

export const GRADIENT = {
  nut: 'linear-gradient(45deg, #42a5f5 30%, #6fccea 90%)',
  dangNhap: 'linear-gradient(90deg, #4dc9e6 0%, #0974f1 100%)',
  hero: 'linear-gradient(120deg, #167ce9 0%, #79efb4 100%)',
  dauThe: 'linear-gradient(90deg, #5ab2f7 0%, #12cff3 100%)',
  sidebarAdmin: 'linear-gradient(180deg, #000428 0%, #043765 100%)',
} as const

export const theme = createTheme({
  palette: {
    primary: { main: MAU.chinh },
    secondary: { main: MAU.xanhSang },
    error: { main: '#ff3d3d' },
    background: { default: MAU.nen },
    text: { primary: MAU.chu },
  },
  typography: {
    fontFamily: 'Roboto, "Helvetica Neue", Arial, sans-serif',
  },
  shape: { borderRadius: 8 },
  components: {
    MuiPaper: { styleOverrides: { rounded: { borderRadius: 12 } } },
    MuiTableCell: { styleOverrides: { head: { fontWeight: 600, color: MAU.chu } } },
  },
})
