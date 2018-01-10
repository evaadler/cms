package org.org.fifi.basic.dao;

import org.fifi.basic.model.Pager;

import java.util.List;
import java.util.Map;

public interface IBaseDao<T> {
    public T add(T t);
    public void update(T t);
    public void delete(int id);
    public T load(int id);

    public List<T> list(String hql, Object[] args);

    public List<T> list(String hql, Object arg);

    public List<T> list(String hql);

    public List<T> list(String hql, Object[] args, Map<String, Object> alias);
    public List<T> listByAlias(String hql, Map<String, Object> alias);

    /**
     * 分页列表对象
     * @param hql 查询对象的hql
     * @param args 查询参数
     * @return 一组分页的列表对象
     */
    public Pager<T> find(String hql, Object[] args);
    public Pager<T> find(String hql, Object arg);
    public Pager<T> find(String hql);

    /**
     * 基于别名和查询参数的混合列表对象
     * @param hql
     * @param args
     * @param alias 别名对象
     * @return
     */
    public Pager<T> find(String hql,Object[] args, Map<String, Object> alias);
    public Pager<T> find(String hql, Map<String, Object> alias);

    /**
     * 根据hql查询一组对象
     * @param hql
     * @param args
     * @return
     */
    public Object queryObject(String hql, Object[] args);
    public Object queryObject(String hql, Object arg);
    public Object queryObject(String hql);
    public Object queryObject(String hql, Object[] args, Map<String, Object> alias);
    public Object queryObject(String hql, Map<String, Object> alias);

    /**
     * 根据hql更新对象
     * @param hql
     * @param args
     */
    public void updateByHql(String hql, Object[] args);
    public void updateByHql(String hql, Object arg);
    public void updateByHql(String hql);

    /**
     * 根据SQL查询对象， 不包含关联对象
     * @param sql
     * @param args 查询条件
     * @param cls 查询的实体对象
     * @param hasEntity 该对象是否是一个hibernate所管理的实体，如果不是需要使用setResultThransform插叙
     * @return 一组对象
     */
    public List<Object> listBySql(String sql, Object[] args, Class<Object> cls, boolean hasEntity);
    public List<Object> listBySql(String sql, Object arg, Class<Object> cls, boolean hasEntity);
    public List<Object> listBySql(String sql, Class<Object> cls, boolean hasEntity);
    public List<Object> listBySql(String sql, Object[] args, Map<String,Object> alias, Class<Object> cls, boolean hasEntity);
    public List<Object> listByAliasSql(String sql, Map<String,Object> alias,Class<Object> cls, boolean hasEntity);

    /**
     * 根据SQL查询对象， 不包含关联对象
     * @param sql
     * @param args 查询条件
     * @param cls 查询的实体对象
     * @param hasEntity 该对象是否是一个hibernate所管理的实体，如果不是需要使用setResultThransform插叙
     * @return 一组对象
     */
    public Pager<Object> findBySql(String sql, Object[] args, Class<Object> cls, boolean hasEntity);
    public Pager<Object> findBySql(String sql, Object arg, Class<Object> cls, boolean hasEntity);
    public Pager<Object> findBySql(String sql, Class<Object> cls, boolean hasEntity);
    public Pager<Object> findBySql(String sql, Object[] args, Map<String,Object> alias, Class<Object> cls, boolean hasEntity);
    public Pager<Object> findByAliasSql(String sql, Map<String,Object> alias,Class<Object> cls, boolean hasEntity);
}
