package com.credit.creditmanagement.mapper;

import com.credit.creditmanagement.entity.SeqNo;
import org.apache.ibatis.annotations.Param;

public interface SeqNoMapper {
    /**
     * 新增序列号
     * @return
     */
    int insertSeqNo(@Param("seqNo") SeqNo seqNo);

}
