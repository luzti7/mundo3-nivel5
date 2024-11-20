/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import model.Produto;


public class ProdutoJpaController {

    private EntityManagerFactory emf;
    
    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Produto> findProdutoEntities() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Produto> cq = em.getCriteriaBuilder().createQuery(Produto.class);
            cq.select(cq.from(Produto.class));
            Query q = em.createQuery(cq);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Produto findProduto(Integer idProduto) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, idProduto);
        } finally {
            em.close();
        }
    }
    
    public void edit(Produto produto) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            produto = em.merge(produto);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
