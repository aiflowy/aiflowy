import docNode from './documentNode.ts'
import makeFileNode from './makeFileNode.ts'
import sqlNode from "./sqlNode.ts";
import downloadNode from "./downloadNode.ts";

export default {
    ...docNode,
    ...makeFileNode,
    ...sqlNode,
    ...downloadNode
}