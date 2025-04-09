import React, {useContext} from "react"
import mitt, {Emitter} from "mitt"

/**
 * 参考 https://github.com/codeshifu/react-mitt
 */
const emitter = mitt()

export interface MittContextType {
    emitter: Emitter<any>
}

const MittContext = React.createContext<MittContextType>({emitter})

export const MittProvider: React.FC<{ children: any }> = ({children}) => {
    return (
        <MittContext.Provider value={{emitter}}>{children}</MittContext.Provider>
    )
}

export const useMitt = (): MittContextType => useContext(MittContext)