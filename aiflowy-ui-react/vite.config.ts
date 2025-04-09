import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import commonjs from "@rollup/plugin-commonjs";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        react(),
        commonjs(),
    ],


    build: {
        commonjsOptions: {include: []},
    },
    server: {
        proxy: {
            '^/api': {
                target: 'http://localhost:8080/',
            },
            '^/attachment/': {
                target: 'http://localhost:8899/public',
            },
        },
    },
})
