package org.pkucare.repository;

import org.pkucare.pojo.CertIDPhoto;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CertIDPhotoRepository extends MongoRepository<CertIDPhoto, Long> {

    @Override
    <S extends CertIDPhoto> List<S> saveAll(Iterable<S> entities);

    @Query("{idCard : ?0}")
    List<CertIDPhoto> queryByIdCard(String idCard);

    CertIDPhoto findFirstByIdCardOrderByUpdateTimeDesc(String idCard);

    List<CertIDPhoto> queryAllByBatchNum(Double batchNum);
}
