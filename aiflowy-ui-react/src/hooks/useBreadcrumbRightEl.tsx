import React, {useEffect} from "react";
import {useLayout} from "./useLayout";

export const useBreadcrumbRightEl = (el: React.ReactNode) => {
    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({
            breadcrumbRightEl: el,
        });
        return () => {
            setOptions({
                breadcrumbRightEl: null,
            });
        }
    }, []);
}
