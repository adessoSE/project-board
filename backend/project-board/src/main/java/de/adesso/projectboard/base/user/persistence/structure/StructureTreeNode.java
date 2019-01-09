package de.adesso.projectboard.base.user.persistence.structure;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.structure.tree.TreeNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "STRUCTURE_TREE_NODE")
@Getter
@Setter(AccessLevel.PROTECTED)
public class StructureTreeNode extends TreeNode<User> {

    boolean managingUser;

    public StructureTreeNode(TreeNode<User> parent, User content) {
        super(parent, content);

        this.managingUser = false;
    }

    public StructureTreeNode(User content) {
        super(content);

        this.managingUser = false;
    }

    @Override
    public void addChild(TreeNode<User> child) throws IllegalArgumentException {
        // TODO: set isManager of child's parent to false when it has no child nodes after removing the child
        // TODO: set isManager to true

        super.addChild(child);
    }

    @Override
    public boolean isLeaf() {
        return managingUser;
    }

}
