// components/Loading.tsx
import React from 'react';
import styles from '../../pages/commons/login.module.less'; // 或使用 CSS 模块
import logo from '../../../public/favicon.png'

const Loading: React.FC = () => {
    return (
        <div className={styles.loadingContainer}>
            <div className={styles.loadingContent}>
                <div className={styles.logoAnimation}>
                    <img src={logo} alt="品牌Logo" className={styles.logo} />
                    <div className={styles.loadingDots}>
                        <span className={styles.dot}></span>
                        <span className={styles.dot}></span>
                        <span className={styles.dot}></span>
                    </div>
                </div>
                <p className={styles.loadingText}>正在为您准备精彩内容...</p>
                <div className={styles.progressBar}>
                    <div className={styles.progress}></div>
                </div>
            </div>
        </div>
    );
};

export default Loading;