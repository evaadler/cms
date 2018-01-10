package org.fifi.basic.dao;

import org.fifi.basic.model.User;
import org.org.fifi.basic.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository()
public class UserDao extends BaseDao<User> implements IUserDao {
}
