import {DependencyList, useEffect, useRef} from 'react';

const useChangesEffect = (func: () => void, deps: DependencyList) => {
    const didMount = useRef(false);

    useEffect(() => {
        if (didMount.current) func();
        else didMount.current = true;
    }, deps);
}

export default useChangesEffect;