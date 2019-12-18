package org.pkucare.repository;

import org.pkucare.pojo.CertificateInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * 数据库操作
 * Created by weiqin on 2019/12/17.
 */
public interface CertificateRepository extends MongoRepository<CertificateInfo, Long> {

    /**
     * 根据 idCard 查询用户
     * @param idCard
     * @return
     */
    @Query("{idCard : ?0}")
    List<CertificateInfo> queryCertByIdCard(String idCard);

    /**
     * 根据 serialNum 查询用户
     * @param serialNum
     * @return
     */
    @Query("{serialNum : ?0}")
    List<CertificateInfo> queryCertBySerialNum(String serialNum);


    /**
     * 根据 telPhone 查询用户
     * @param phone
     * @return
     */
    @Query("{phone : ?0}")
    List<CertificateInfo> queryCertByPhone(String phone);
}
