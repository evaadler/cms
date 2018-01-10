package org.org.fifi.basic.dao;

import org.fifi.basic.model.Pager;
import org.fifi.basic.model.SystemContext;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.*;

public class BaseDao<T> implements IBaseDao<T> {


    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    //@Resource  inject 代替掉Resource
    @Inject
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.openSession();
    }


    /**
     * 创建一个Class的对象来获取泛型的class
     */
    private Class<T> clz;

    public Class<T> getClz() {
        if (clz == null) {
            // 获取泛型的clazz对象
            Type[] t = ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments();
            clz = (Class<T>)t[0];
        }
        return clz;
    }

    @Override
    public T add(T t) {
        getSession().save(t);
        return t;
    }

    @Override
    public void update(T t) {
        getSession().update(t);
    }

    @Override
    public void delete(int id) {

        getSession().delete(this.load(id));
    }

    @Override
    public T load(int id) {
        return (T) getSession().load(getClz(), id);
    }

    @Override
    public List<T> list(String hql, Object[] args) {

        return this.list(hql, args, null);
    }

    @Override
    public List<T> list(String hql, Object arg) {
        Object[] args = new Object[1];
        args[0] = arg;
       // return this.list(hql, args, null);

        return this.list(hql, new Object[]{arg});
    }

    @Override
    public List<T> list(String hql) {
        return this.list(hql, null);
    }

    @Override
    public List<T> list(String hql, Object[] args, Map<String, Object> alias) {

        hql = initSort(hql);

        Query query = getSession().createQuery(hql);

        //视频中不返回query可以改编值吗？
        query = setAliasParameter(query, alias);


        query = setParameter(query, args);

        return query.list();
    }

    private Query setParameter(Query query, Object[] args){
        if (args != null && args.length > 0) {
            int index = 0;
            for (Object arg : args) {
                query.setParameter(index++, arg);
            }
        }
        return query;
    }

    private Query setAliasParameter(Query query, Map<String, Object> alias){
        if (alias != null) {
            Set<String> keys = alias.keySet();
            for (String key : keys) {
                Object val = alias.get(key);
                // 查询条件是列表
                if (val instanceof Collection) {
                    query.setParameterList(key, (Collection) val);
                } else {
                    query.setParameter(key, val);
                }
            }
        }
        return query;
    }

    private String initSort(String hql) {
        String order = SystemContext.getOrder();
        String sort = SystemContext.getSort();
        if (sort != null && !"".equals(sort.trim())) {
            hql += " order by " + sort;
            if (!"desc".equals(order)) hql += " asc";
            else hql += " desc";
        }
        return hql;
    }



    @Override
    public List<T> listByAlias(String hql, Map<String, Object> alias) {
        return this.list(hql, null, alias);
    }



    /**
     * 分页列表对象
     *
     * @param hql  查询对象的hql
     * @param args 查询参数
     * @return 一组分页的列表对象
     */
    @Override
    public Pager<T> find(String hql, Object[] args) {
        return this.find(hql, args);
    }

    @Override
    public Pager<T> find(String hql, Object arg) {
        return this.find(hql, new Object[]{arg});
    }

    @Override
    public Pager<T> find(String hql) {
        return this.find(hql, null, null);
    }

    /**
     * 基于别名和查询参数的混合列表对象
     *
     * @param hql
     * @param args
     * @param alias 别名对象
     * @return
     */
    @Override
    public Pager<T> find(String hql, Object[] args, Map<String, Object> alias) {
        hql = initSort(hql);
        String cq = getCountHql(hql, true);
        cq = initSort(cq);
        Query cquery = getSession().createQuery(hql);
        Query query =  getSession().createQuery(hql);
        // 设置别名
        setAliasParameter(query, alias);
        setAliasParameter(cquery, alias);
        // 设置参数
        setParameter(query, args);
        setParameter(cquery, args);

        Pager<T> pages = new Pager<T>();
        setPagers(query, pages);

        List<T> datas = query.list();
        pages.setDatas(datas);

        long total = (Long)cquery.uniqueResult();
        pages.setTotal(total);

        return pages;
    }

    private void setPagers(Query query, Pager pages) {
        Integer pageSize = SystemContext.getPageSize();
        Integer pageOffset = SystemContext.getPageOffset();

        if (pageOffset==null || pageOffset<0) pageOffset = 0;
        if (pageSize==null || pageSize<0) pageSize=0;

        pages.setOffset(pageOffset);
        pages.setSize(pageSize);

        query.setFirstResult(pageOffset).setMaxResults(pageSize);
    }

    private String getCountHql(String hql, boolean isHql ) {
        String e = hql.substring(hql.indexOf("from"));
        String c = "select count(*)" + e;
        if (isHql)
            c.replace("fetch", "");
        return c;
    }


    @Override
    public Pager<T> find(String hql, Map<String, Object> alias) {
        return this.find(hql, null, alias);
    }

    /**
     * 根据hql查询一组对象
     *
     * @param hql
     * @param args
     * @return
     */
    @Override
    public Object queryObject(String hql, Object[] args) {
        Query query = getSession().createQuery(hql);
        return null;
    }

    @Override
    public Object queryObject(String hql, Object[] args, Map<String, Object> alias) {

        Query query = getSession().createQuery(hql);

        setAliasParameter(query, alias);
        setParameter(query, args);

        return query.uniqueResult();
    }

    @Override
    public Object queryObject(String hql, Map<String, Object> alias) {
        return this.queryObject(hql, null, alias);
    }

    @Override
    public Object queryObject(String hql, Object arg) {
        return this.queryObject(hql, new Object[]{arg}, null);
    }

    @Override
    public Object queryObject(String hql) {
        return this.queryObject(hql, null, null);
    }

    /**
     * 根据hql更新对象
     *
     * @param hql
     * @param args
     */
    @Override
    public void updateByHql(String hql, Object[] args) {
        Query query = getSession().createQuery(hql);
        setParameter(query, args);
        query.executeUpdate();
    }

    @Override
    public void updateByHql(String hql, Object arg) {
        this.updateByHql(hql, new Object[]{arg});
    }

    @Override
    public void updateByHql(String hql) {
        this.updateByHql(hql, null);
    }

    /**
     * 根据SQL查询对象， 不包含关联对象
     *
     * @param sql
     * @param args      查询条件
     * @param cls       查询的实体对象
     * @param hasEntity 该对象是否是一个hibernate所管理的实体，如果不是需要使用setResultThransform插叙
     * @return 一组对象
     */
    @Override
    public List<Object> listBySql(String sql, Object[] args, Class<Object> cls, boolean hasEntity) {
        return this.listBySql(sql, args, null, cls, hasEntity);
    }

    @Override
    public List<Object> listBySql(String sql, Object arg, Class<Object> cls, boolean hasEntity) {
        return this.listBySql(sql, new Object[]{arg}, null, cls, hasEntity);
    }

    @Override
    public List<Object> listBySql(String sql, Class<Object> cls, boolean hasEntity) {
        return this.listBySql(sql, null, null, cls, hasEntity);
    }

    @Override
    public List<Object> listBySql(String sql, Object[] args, Map<String, Object> alias, Class<Object> cls, boolean hasEntity) {
        sql = initSort(sql);
        SQLQuery sq = getSession().createSQLQuery(sql);

        setAliasParameter(sq, alias);
        setParameter(sq, args);

        if (hasEntity){
            sq.addEntity(clz);
        }else {
            sq.setResultTransformer(Transformers.aliasToBean(clz));
        }

        return null;
    }

    @Override
    public List<Object> listByAliasSql(String sql, Map<String, Object> alias, Class<Object> cls, boolean hasEntity) {
        return this.listBySql(sql, null, alias, cls, hasEntity);
    }

    /**
     * 根据SQL查询对象， 不包含关联对象
     *
     * @param sql
     * @param args      查询条件
     * @param cls       查询的实体对象
     * @param hasEntity 该对象是否是一个hibernate所管理的实体，如果不是需要使用setResultThransform插叙
     * @return 一组对象
     */
    @Override
    public Pager<Object> findBySql(String sql, Object[] args, Class<Object> cls, boolean hasEntity) {
        return this.findBySql(sql, args, null, cls, hasEntity);
    }

    @Override
    public Pager<Object> findBySql(String sql, Object arg, Class<Object> cls, boolean hasEntity) {
        return this.findBySql(sql, new Object[]{arg}, null, cls, hasEntity);
    }

    @Override
    public Pager<Object> findBySql(String sql, Class<Object> cls, boolean hasEntity) {
        return this.findBySql(sql, null, null, cls, hasEntity);
    }

    @Override
    public Pager<Object> findBySql(String sql, Object[] args, Map<String, Object> alias, Class<Object> cls, boolean hasEntity) {
        String cq = getCountHql(sql, false);
        cq = initSort(cq);
        sql = initSort(sql);
        Query sq = getSession().createSQLQuery(sql);
        Query cquery = getSession().createSQLQuery(cq);

        setAliasParameter(sq, alias);
        setAliasParameter(cquery, alias);

        setParameter(sq, args);
        setParameter(cquery, args);

        Pager<Object> pages = new Pager<Object>();
        setPagers(sq, pages);

        if (hasEntity){
            //sq.setEntity(clz);
        }else {
            sq.setResultTransformer(Transformers.aliasToBean(clz));
        }



        List<Object> datas = sq.list();
        pages.setDatas(datas);

        long total = (Long)cquery.uniqueResult();
        pages.setTotal(total);

        return pages;
    }

    @Override
    public Pager<Object> findByAliasSql(String sql, Map<String, Object> alias, Class<Object> cls, boolean hasEntity) {
        return this.findBySql(sql, null, alias, cls, hasEntity);
    }


}
