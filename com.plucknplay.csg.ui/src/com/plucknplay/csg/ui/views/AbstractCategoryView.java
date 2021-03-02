/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.core.model.workingCopies.IntervalContainerWorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.CollapseAllAction;
import com.plucknplay.csg.ui.actions.ExpandAllAction;
import com.plucknplay.csg.ui.actions.LinkWithEditorAction;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.editors.input.CategorizableEditorInput;
import com.plucknplay.csg.ui.util.CategoryTreeContentProvider;
import com.plucknplay.csg.ui.util.MyFocusListener;
import com.plucknplay.csg.ui.util.StatusLineUtil;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.dnd.ModelObjectDragListener;
import com.plucknplay.csg.ui.views.dnd.ModelObjectDropAdapter;
import com.plucknplay.csg.ui.views.dnd.ModelObjectTransfer;

public abstract class AbstractCategoryView extends ViewPart implements IChangeListener, IRenameableView,
		ICollapsableView, IExpandableView, ISelectionChangedListener {

	protected static final String POPUP_NEW_GROUP = "newGroup"; //$NON-NLS-1$
	protected static final String POPUP_EDIT_GROUP = "editGroup"; //$NON-NLS-1$
	protected static final String POPUP_IMPORT_GROUP = "importGroup"; //$NON-NLS-1$
	protected static final String POPUP_BOTTOM_GROUP = "bottomGroup"; //$NON-NLS-1$

	private TreeViewer viewer;
	private boolean modifyComponent;
	private LinkWithEditorAction linkWithEditorAction;
	private WorkingCopy workingCopy;
	private ViewForm firstViewForm;
	private List<IViewSelectionAction> actions;

	public void createCategoryViewer(final Composite parent) {

		// create viewer
		firstViewForm = new ViewForm(parent, SWT.NONE);
		viewer = new TreeViewer(firstViewForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new CategoryTreeContentProvider(getCategoryList()));
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setSorter(new ViewerSorter() {
			@Override
			public int category(final Object element) {
				if (element instanceof Category) {
					return 0;
				}
				return 1;
			}
		});
		viewer.setInput(getCategoryList().getRootCategory());
		firstViewForm.setContent(viewer.getControl());

		// double click listener
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final ISelection sel = viewer.getSelection();
				if (sel instanceof StructuredSelection) {
					final Object first = ((StructuredSelection) sel).getFirstElement();
					if (first != null && first instanceof Categorizable) {
						try {
							final Categorizable element = (Categorizable) first;
							final Category category = getCategoryList().getRootCategory().getCategory(element);
							final IWorkbenchWindow window = getSite().getWorkbenchWindow();
							WorkbenchUtil.showPerspective(window.getWorkbench(),
									Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
							getSite().getPage().openEditor(getEditorInput(element, category, false), getEditorID());
						} catch (final PartInitException e) {
						} catch (final WorkbenchException e) {
						}
					} else {
						viewer.setExpandedState(first, !viewer.getExpandedState(first));
					}
				}
			}
		});

		// cell editor
		final CellEditor editor = new TextCellEditor(viewer.getTree());
		editor.addListener(new ICellEditorListener() {
			@Override
			public void applyEditorValue() {
				dispose(false);
			}

			@Override
			public void cancelEditor() {
				dispose(true);
				workingCopy = null;
			}

			private void dispose(final boolean canceled) {
				modifyComponent = false;
				final Object firstElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (firstElement instanceof Categorizable) {
					final Categorizable element = (Categorizable) firstElement;
					if (canceled) {
						workingCopy.setName(element.getName());
					}
					WorkingCopyManager.getInstance().disposeWorkingCopy(element);
				}
			}

			@Override
			public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
				if (workingCopy != null && editor.getValue() instanceof String) {
					workingCopy.setName((String) editor.getValue());
				}
			}
		});
		viewer.setCellEditors(new CellEditor[] { editor });

		// cell modifier
		viewer.setColumnProperties(new String[] { "NAME" }); //$NON-NLS-1$
		viewer.setCellModifier(new ICellModifier() {
			@Override
			public boolean canModify(final Object element, final String property) {
				return modifyComponent;
			}

			@Override
			public Object getValue(final Object element, final String property) {
				if (element instanceof Categorizable) {
					if (workingCopy == null) {
						return ((Categorizable) element).getName();
					}
					return workingCopy.getName();
				} else if (element instanceof Category) {
					return ((Category) element).getName();
				}
				return ""; //$NON-NLS-1$
			}

			@Override
			public void modify(final Object obj, final String property, final Object value) {

				final Object element = obj instanceof TreeItem ? ((TreeItem) obj).getData() : obj;

				if (value instanceof String) {
					final String newName = (String) value;
					if (element instanceof Categorizable) {
						final Categorizable theElement = (Categorizable) element;
						if (workingCopy instanceof IntervalContainerWorkingCopy) {
							((IntervalContainerWorkingCopy) workingCopy).setName(workingCopy.getName(), true);
						}
						if (!workingCopy.saveName()) {
							MessageDialog.openError(getSite().getShell(), ViewMessages.CategoryView_error_title,
									ViewMessages.CategoryView_error_msg_1 + ViewMessages.CategoryView_error_msg_2);
							viewer.setSelection(new StructuredSelection(theElement));
							workingCopy.setName(theElement.getName());
							if (workingCopy instanceof IntervalContainerWorkingCopy) {
								((IntervalContainerWorkingCopy) workingCopy).setAlsoKnownAsString(
										((IntervalContainer) theElement).getAlsoKnownAsString(), false);
							}
							return;
						}
						// set new name
						workingCopy = null;
						modifyComponent = false;
						getCategoryList().changedElement(theElement);

					} else if (element instanceof Category) {
						final Category category = (Category) element;
						// check whether another category with this name already
						// exists
						final Category parentCategory = getCategoryList().getRootCategory().getCategory(category);
						final Category categoryWithSameName = parentCategory.getCategory(newName);
						if (categoryWithSameName != null && categoryWithSameName != category) {
							MessageDialog.openError(getSite().getShell(), ViewMessages.CategoryView_error_title,
									ViewMessages.CategoryView_error_msg_3 + ViewMessages.CategoryView_error_msg_4);
							viewer.setSelection(new StructuredSelection(category));
							return;
						}
						// set new name
						category.setName(newName);
						getCategoryList().changedElement(category);
						modifyComponent = false;
					}
				}
			}
		});

		// focus listener
		viewer.getControl().addFocusListener(new MyFocusListener(this));

		// drag & drop support
		final int ops = getDNDOpererations();
		final Transfer[] transfer = new Transfer[] { ModelObjectTransfer.getInstance(getDNDTransferType()) };
		viewer.addDragSupport(ops, transfer, new ModelObjectDragListener(viewer, getDNDTransferType()));
		viewer.addDropSupport(ops, transfer, getDropAdapter(viewer));

		// last initializations
		getSite().setSelectionProvider(viewer);
		viewer.addSelectionChangedListener(this);
		getCategoryList().addChangeListener(this);
		createContextMenu();
		contributeToActionBars();
		updateContentDescription();
	}

	protected void addAction(final IViewSelectionAction action) {
		if (actions == null) {
			actions = new ArrayList<IViewSelectionAction>();
		}
		actions.add(action);
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (event != null && event.getSelection() instanceof IStructuredSelection) {
			for (final IViewSelectionAction action : actions) {
				action.selectionChanged(event.getSelection());
			}
		}
	}

	private void updateContentDescription() {
		setContentDescription(""); //$NON-NLS-1$

		final String elementName = getCategoryList() == InstrumentList.getInstance() ? ViewMessages.CategoryView_instruments
				: getCategoryList() == ChordList.getInstance() ? ViewMessages.CategoryView_chords
						: ViewMessages.CategoryView_scales;

		final String text = NLS.bind(ViewMessages.CategoryView_elements_in_folder, new Object[] {
				getCategoryList().getRootCategory().getAllElements().size(), elementName,
				getCategoryList().getRootCategory().getAllCategories().size() });
		setContentDescription(text);
	}

	/**
	 * Creates the link with editor action and adds the necessary listeners.
	 */
	private void createLinkWithEditorAction() {
		linkWithEditorAction = new LinkWithEditorAction(getCategoryList());
		linkWithEditorAction.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (event.getSource() == linkWithEditorAction && event.getProperty() == IAction.CHECKED
						&& linkWithEditorAction.isChecked()) {
					final IEditorPart editor = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
					if (editor != null && editor.getEditorInput() instanceof CategorizableEditorInput) {
						final CategorizableEditorInput input = (CategorizableEditorInput) editor.getEditorInput();
						final Categorizable element = input.getElement();
						if (input.isNewElement()) {
							return;
						}
						final CategoryList categoryList = element instanceof Instrument ? InstrumentList.getInstance()
								: element instanceof Chord ? ChordList.getInstance() : ScaleList.getInstance();
						expandElements(categoryList.getRootCategory().getCategoryPath(element, true));
					}
				}
			}
		});
	}

	/**
	 * Contribute to menu and toolbar.
	 */
	protected void contributeToActionBars() {

		// create actions
		createLinkWithEditorAction();
		final CollapseAllAction collapseAllAction = new CollapseAllAction(this);
		final ExpandAllAction expandAllAction = new ExpandAllAction(this);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), expandAllAction);
		activator.registerAction(getSite(), collapseAllAction);
		activator.registerAction(getSite(), linkWithEditorAction);

		// contribute actions and group markers
		final IActionBars bars = getViewSite().getActionBars();

		bars.getMenuManager().add(new Separator("additions_left")); //$NON-NLS-1$
		bars.getMenuManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getMenuManager().add(new Separator("additions_right")); //$NON-NLS-1$
		bars.getMenuManager().add(new Separator("additions_right_2")); //$NON-NLS-1$
		bars.getMenuManager().appendToGroup("additions_right", expandAllAction); //$NON-NLS-1$
		bars.getMenuManager().appendToGroup("additions_right", collapseAllAction); //$NON-NLS-1$
		bars.getMenuManager().appendToGroup("additions_right", linkWithEditorAction); //$NON-NLS-1$

		bars.getToolBarManager().add(new Separator("additions_left")); //$NON-NLS-1$
		bars.getToolBarManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		bars.getToolBarManager().add(new Separator("additions_right")); //$NON-NLS-1$
		bars.getToolBarManager().add(new Separator("additions_right_2")); //$NON-NLS-1$
		bars.getToolBarManager().appendToGroup("additions_right", expandAllAction); //$NON-NLS-1$
		bars.getToolBarManager().appendToGroup("additions_right", collapseAllAction); //$NON-NLS-1$
		bars.getToolBarManager().appendToGroup("additions_right", linkWithEditorAction); //$NON-NLS-1$
	}

	/**
	 * Creates the context menu.
	 */
	private void createContextMenu() {
		// create menu manager
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager mgr) {
				mgr.add(new Separator(POPUP_NEW_GROUP));
				mgr.add(new Separator(POPUP_EDIT_GROUP));
				mgr.add(new Separator(POPUP_IMPORT_GROUP));
				mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				mgr.add(new GroupMarker(POPUP_BOTTOM_GROUP));
				addToContextMenu(mgr);
			}
		});

		// create menu.
		final Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// register menu for extension.
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void addToContextMenu(final IMenuManager mgr) {
	}

	protected void setTopLeft(final Control control) {
		firstViewForm.setTopLeft(control);
	}

	protected void setTopRight(final Control control) {
		firstViewForm.setTopRight(control);
	}

	protected void setTopCenter(final Control control) {
		firstViewForm.setTopCenter(control);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
		StatusLineUtil.clearStatusLine(getViewSite());
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		if (property == CategoryList.PROP_ADDED) {
			if (modifyComponent) {
				viewer.cancelEditing();
				modifyComponent = false;
			}
			viewer.add(parentSource, source);
			expandElement(source, parentSource);
			updateContentDescription();
		} else if (property == CategoryList.PROP_REMOVED) {
			viewer.remove(source);
			updateContentDescription();
		} else if (property == CategoryList.PROP_CHANGED_ELEMENT) {
			// it has to be refreshed the whole viewer since a rename may affect
			// the sorting
			viewer.refresh(true);
		} else if (property == CategoryList.PROP_CHANGED_WHOLE_LIST) {
			viewer.refresh(true);
			updateContentDescription();
		} else if (property == CategoryList.PROP_MOVED) {
			viewer.remove(source);
			viewer.add(parentSource, source);
		}
	}

	/**
	 * Refreshs the viewer and reproduces the same expanded state.
	 */
	protected void refreshViewer() {
		final Object[] elements = viewer.getExpandedElements();
		viewer.refresh(true);
		viewer.setExpandedElements(elements);
	}

	@Override
	public void dispose() {
		if (viewer != null) {
			viewer.removeSelectionChangedListener(this);
		}
		getCategoryList().removeChangeListener(this);
		getSite().setSelectionProvider(null);
		super.dispose();
	}

	/**
	 * Enables the cell editor to perform the renaming of the selected element.
	 */
	@Override
	public void performRenaming() {
		if (!modifyComponent && viewer.getSelection() instanceof IStructuredSelection) {
			modifyComponent = true;
			final Object firstElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
			if (firstElement instanceof Categorizable) {
				final Categorizable element = (Categorizable) firstElement;
				final Category category = getCategoryList().getRootCategory().getCategory(element);
				workingCopy = WorkingCopyManager.getInstance().getWorkingCopy(element, category, false);
			}
			viewer.editElement(firstElement, 0);
		}
	}

	@Override
	public void expandElement(final Object element, final Object parentElement) {
		viewer.setExpandedState(parentElement, true);
		viewer.setSelection(new StructuredSelection(element), true);
	}

	/**
	 * Expands the given list of elements.
	 * 
	 * Note: This method is intended to be called only by linked with editor
	 * specific actions.
	 * 
	 * @param elements
	 *            the list of elements to expand, must not be null
	 */
	public void expandElements(final List<?> elements) {
		if (linkWithEditorAction == null || !linkWithEditorAction.isChecked() || elements == null || elements.isEmpty()) {
			return;
		}

		Object lastElement = elements.get(0);
		for (final Iterator<?> iter = elements.iterator(); iter.hasNext();) {
			lastElement = iter.next();
			viewer.setExpandedState(lastElement, true);
		}
		viewer.setSelection(new StructuredSelection(lastElement), true);
	}

	public ViewForm getFirstViewForm() {
		return firstViewForm;
	}

	protected Viewer getViewer() {
		return viewer;
	}

	@Override
	public void collapseAll() {
		if (viewer != null) {
			viewer.collapseAll();
		}
	}

	@Override
	public void expandAll() {
		if (viewer != null) {
			viewer.expandAll();
		}
	}

	protected abstract CategoryList getCategoryList();

	protected abstract IEditorInput getEditorInput(Categorizable element, Category category, boolean newElement);

	protected abstract String getEditorID();

	protected abstract int getDNDOpererations();

	protected abstract Object getDNDTransferType();

	protected abstract ModelObjectDropAdapter getDropAdapter(TreeViewer viewer);
}
